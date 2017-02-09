package org.cmapi.mirrorcache.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.cmapi.mirrorcache.Member;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.MirrorCacheException.Reason;
import org.cmapi.mirrorcache.channel.Channel;
import org.cmapi.mirrorcache.channel.Channel.Flow;
import org.cmapi.mirrorcache.channel.Channel.Type;
import org.cmapi.mirrorcache.service.event.ChannelDeletedEvent;
import org.cmapi.mirrorcache.service.event.DisconnectEvent;
import org.cmapi.primitives.proto.CmapiProto.ChannelInfo;
import org.cmapi.primitives.proto.CmapiProto.MemberInfo;
import org.slf4j.Logger;

@ApplicationScoped
public class ChannelManager {

    @Inject
    private Logger LOG;
    
    @Inject
    private ChannelGroupManager channelGroupManager;
    
    @Inject
    private Event<ChannelDeletedEvent> channelDeleteEvent;
    
    private Map<String, ServerChannel> channelMap; // map<channelName, channel>
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    @PostConstruct
    public void init() {
        channelMap = new HashMap<>();
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    public void sessionClosed(@Observes DisconnectEvent event) {
        LOG.info("...sessionClosed()");

        final Member disconnectedMember = new Member(event.getSessionId());
        
        for (Iterator<Map.Entry<String, ServerChannel>> iter = channelMap.entrySet().iterator(); iter.hasNext(); ) {
            final Map.Entry<String, ServerChannel> entry = iter.next();
            
            final ServerChannel channel = entry.getValue();
            
            /*
             * remove all temporary channels owned by event.sessionId
             */
            if (channel.getType() == Type.TEMPORARY
                    && channel.getOwnerId().equals(event.getSessionId())) {

                iter.remove();
                channelDeleteEvent.fire(new ChannelDeletedEvent(channel));
                continue;
            }
            
            /*
             * Remove disconnectedMember from all channels.
             */
            if (channel.getMembers().contains(disconnectedMember)) {
                channel.removeMember(disconnectedMember);
            }
        }
    }
    
    public List<ChannelInfo> findChannels(String sessionId, String filter) {
        final List<ChannelInfo> results = new ArrayList<>();
        
        for (Map.Entry<String, ServerChannel> entry : channelMap.entrySet()) {
            final ServerChannel channel = entry.getValue();
            
            /*
             * Retrieve only public channels and those created by the current session.
             */
            if (channel.getVisibility() == Channel.Visibility.PUBLIC
                    || channel.getOwnerId().equals(sessionId)) {
                
                final boolean isOpen = channel.isOpen(new Member(sessionId));
                
                //TODO filter
                results.add(ChannelInfo.newBuilder()
                                       .setName(channel.getName())
                                       .setVisibility(channel.getVisibility().name())
                                       .setType(channel.getType().name())
                                       .setIsOpen(isOpen)
                                       .build());
            }
        }
        
        return results;
    }
    
    public void createChannel(String sessionId, String channelName, Channel.Visibility visibility, Channel.Type type) throws MirrorCacheException {
        if (!channelMap.containsKey(channelName)) {
            channelMap.put(channelName, new ServerChannel(sessionId, channelName, visibility, type));
            
        } else {
            throw new MirrorCacheException(Reason.CHANNEL_ALREADY_EXISTS).withDetail("channelName: " + channelName);
        }
    }
    
    public void deleteChannel(String sessionId, String channelName) throws MirrorCacheException {
        final Channel channel = get(channelName);
        channelMap.remove(channel.getName());
        
        channelDeleteEvent.fire(new ChannelDeletedEvent(channel));
    }
    
    /** Opens a channel for the supplied session. */
    public void channelOpen(String sessionId, String channelName, Flow flow, String filter) throws MirrorCacheException {
        final ServerChannel channel = get(channelName);
        
        final Member member = new Member(sessionId);
        
        /*
         * Add member to channel.
         */
        if (channel.getMembers().contains(member)) {
            throw new MirrorCacheException(Reason.CHANNEL_ALREADY_OPEN)
                                                 .withDetail("member: " + member)
                                                 .withDetail("channelName: " + channelName);
        }
        channel.addMember(member);
    }
    
    //TODO void onChannelClosed
    // void onChannelOpened
    // void onChannelCreated
    //TODO fire event indicating channels has been closed..
    //     will have the effect of sending messages to all currently connected clients
    //     and disconnecting them and all state data(?)
    
    /** Closes a channel for the supplied session */
    public void channelClose(String sessionId, String channelName) throws MirrorCacheException {
        final ServerChannel channel = get(channelName);
        
        final Member member = new Member(sessionId);
        channel.removeMember(member);
    }
    
    public Set<Member> channelPublish(String sessionId, String channelName) throws MirrorCacheException {
        final Set<Member> results = new HashSet<>();
        
        final ServerChannel channel = get(channelName);
        
        /*
         * Add all members of channel.
         */
        results.addAll(channel.getMembers());
        
        /*
         * Add all members of each group this channel is a part of.
         */
        final List<ServerChannelGroup> channelGroups = channelGroupManager.findChannelGroupsWithChannel(sessionId, channel);
        for (ServerChannelGroup channelGroup : channelGroups) {
            results.addAll(channelGroup.getMembers());
        }
        
        /*
         * Exclude the current session from results.
         */
        final Member currentMember = new Member(sessionId);
        results.remove(currentMember);
        
        return results;
    }

    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    public ServerChannel get(String channelName) throws MirrorCacheException {
        final ServerChannel channel = channelMap.get(channelName);
        if (channel == null) {
            throw new MirrorCacheException(Reason.CHANNEL_DNE).withDetail("channelName: " + channelName);
        }
        return channel;
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    public List<ChannelInfo> statusChannels() {
        final List<ChannelInfo> status = new ArrayList<>();
        
        for (ServerChannel channel : channelMap.values()) {
            
            final List<MemberInfo> memberInfos = new ArrayList<>();
            
            for (Member member : channel.getMembers()) {
                memberInfos.add(MemberInfo.newBuilder().setSessionId(member.getSessionId()).build());
            }
            
            status.add(ChannelInfo.newBuilder()
                                  .setName(channel.getName())
                                  .setType(channel.getType().name())
                                  .setVisibility(channel.getVisibility().name())
                                  .addAllMember(memberInfos)
                                  .build());
        }
        return status;
    }
}
