package mil.emp3.mirrorcache.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.cmapi.primitives.proto.CmapiProto.ChannelGroupInfo;
import org.cmapi.primitives.proto.CmapiProto.ChannelInfo;
import org.cmapi.primitives.proto.CmapiProto.MemberInfo;

import mil.emp3.mirrorcache.Member;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.service.event.ChannelDeletedEvent;
import mil.emp3.mirrorcache.service.event.ChannelGroupDeletedEvent;
import mil.emp3.mirrorcache.service.event.DisconnectEvent;

@ApplicationScoped
public class ChannelGroupManager {
    
    final private String MAGIC = UUID.randomUUID().toString();
    
    @Inject
    private ChannelManager channelManager;
    
    @Inject
    private Event<ChannelGroupDeletedEvent> channelGroupDeleteEvent;
    
    private Map<String, ServerChannelGroup> channelGroupMap; // map<channelGroupName, channelGroup>
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    @PostConstruct
    public void init() {
        channelGroupMap = new HashMap<>();
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    public void onChannelDeleted(@Observes ChannelDeletedEvent event) {
        final ServerChannel deletedChannel = (ServerChannel) event.getChannel();

        /*
         * Remove channel from groups.
         */
        for (ServerChannelGroup channelGroup : channelGroupMap.values()) {
            if (channelGroup.getChannels().contains(deletedChannel)) {
                channelGroup.removeChannel(deletedChannel);
                continue;
            }
        }
    }
    
    public void sessionClosed(@Observes DisconnectEvent event) {
        for (Iterator<ServerChannelGroup> iter = channelGroupMap.values().iterator(); iter.hasNext(); ) {
            final ServerChannelGroup channelGroup = iter.next();
            
            /*
             * Remove channelGroup if owner disconnects.
             */
            if (channelGroup.getOwnerId().equals(event.getSessionId())) {
                iter.remove();
                continue;
            }
            
            /*
             * Remove current session from memberLists
             */
            final Member member = new Member(event.getSessionId());
            if (channelGroup.getMembers().contains(member)) {
                channelGroup.removeMember(member);
            }
        }
    }
    
    public List<ServerChannelGroup> findChannelGroupsWithChannel(String sessionId, ServerChannel channel) {
        final List<ServerChannelGroup> results = new ArrayList<>();
        
        for (ServerChannelGroup channelGroup : channelGroupMap.values()) {
            if (channelGroup.getChannels().contains(channel)) {
                results.add(channelGroup);
            }
        }
        
        return results;
    }
    
    public List<ChannelGroupInfo> findChannelGroups(String sessionId, String filter) {
        final List<ChannelGroupInfo> results = new ArrayList<>();
        
        final Member currentMember = new Member(sessionId);
        
        for (Map.Entry<String, ServerChannelGroup> entry : channelGroupMap.entrySet()) {
            final ServerChannelGroup channelGroup = entry.getValue();
            
            final List<ChannelInfo> channelInfos = new ArrayList<>();
            for (ServerChannel channel : channelGroup.getChannels()) {
                
                final boolean isOpen = channel.isOpen(currentMember);

                channelInfos.add(ChannelInfo.newBuilder().setName(channel.getName())
                                                         .setVisibility(channel.getVisibility().name())
                                                         .setType(channel.getType().name())
                                                         .setIsOpen(isOpen)
                                                     .build());
            }
            
            final List<MemberInfo> memberInfos = new ArrayList<>();
            for (Member member : channelGroup.getMembers()) {
                memberInfos.add(MemberInfo.newBuilder().setSessionId(member.getSessionId()).build());
            }
            
            final boolean isJoined = channelGroup.isJoined(currentMember);
            
            //TODO filter
            results.add(ChannelGroupInfo.newBuilder().setName(channelGroup.getName())
                                                     .addAllChannel(channelInfos)
                                                     .addAllMember(memberInfos)
                                                     .setIsJoined(isJoined)
                                                 .build());
        }
        
        return results;
    }
    
    public void createChannelGroup(String sessionId, String channelGroupName) throws MirrorCacheException {
        if (!channelGroupMap.containsKey(channelGroupName)) {
            channelGroupMap.put(channelGroupName, new ServerChannelGroup(sessionId, channelGroupName));
            
        } else {
            throw new MirrorCacheException(Reason.CHANNELGROUP_ALREADY_EXISTS).withDetail("channelGroupName: " + channelGroupName);
        }
    }
    
    public void deleteChannelGroup(String sessionId, String channelGroupName) throws MirrorCacheException {
        final ServerChannelGroup channelGroup = get(channelGroupName);
        channelGroupMap.remove(channelGroup.getName());
        
        channelGroupDeleteEvent.fire(new ChannelGroupDeletedEvent(channelGroup));
    }
    
    /** Determines which members should receive data when publishing to this group. */
    public Set<Member> publish(String channelGroupName) throws MirrorCacheException {
        final Set<Member> results = new HashSet<>();
        
        final ServerChannelGroup channelGroup = get(channelGroupName);
        
        /*
         * Collect members of this group.
         */
        results.addAll(channelGroup.getMembers());
        
        /*
         * Collect members of the channels in this group.
         */
        for (ServerChannel channel : channelGroup.getChannels()) {
            results.addAll(channel.getMembers());
        }
        
        return results;
    }
    
    /** Removes a channel from a channelGroup. */
    public void channelGroupRemoveChannel(String sessionId, String channelGroupName, String channelName) throws MirrorCacheException {
        final ServerChannelGroup channelGroup = get(channelGroupName);
        
        /*
         * Only the owner of the channelGroup may remove channels.
         */
        if (!(channelGroup.getOwnerId().equals(sessionId) || sessionId.equals(MAGIC))) {
            throw new MirrorCacheException(Reason.CHANNELGROUP_NOT_OWNER).withDetail("channelGroupName: " + channelGroupName);
        }
        
        final ServerChannel channel = channelManager.get(channelName);
        
        /*
         * Remove channel from group.
         */
        if (!channelGroup.getChannels().contains(channel)) {
            throw new MirrorCacheException(Reason.CHANNELGROUP_CHANNEL_NOT_FOUND)
                                                 .withDetail("channelGroupName: " + channelGroupName)
                                                 .withDetail("channelName: " + channelName);
        }
        channelGroup.removeChannel(channel);
    }
    
    /** Adds a channel to a channelGroup and sets the channel to open. */
    public void channelGroupAddChannel(String sessionId, String channelGroupName, String channelName) throws MirrorCacheException {
        final ServerChannelGroup channelGroup = get(channelGroupName);
        
        /*
         * Only the owner of the channelGroup may add channels.
         */
        if (!channelGroup.getOwnerId().equals(sessionId)) {
            throw new MirrorCacheException(Reason.CHANNELGROUP_NOT_OWNER).withDetail("channelGroupName: " + channelGroupName);
        }
        
        
        final ServerChannel channel = channelManager.get(channelName);
        
        /*
         * Ensure the channel hasn't already been added.
         */
        if (channelGroup.getChannels().contains(channel)) {
            throw new MirrorCacheException(Reason.CHANNELGROUP_CHANNEL_ALREADY_EXISTS)
                                                 .withDetail("channelGroupName: " + channelGroupName)
                                                 .withDetail("channelName: " + channelName);
        }
        channelGroup.addChannel(channel);
    }
    
    /** Add this sessionId to the memberList for this channelGroup. */
    public void channelGroupJoin(String sessionId, String channelGroupName) throws MirrorCacheException {
        final ServerChannelGroup channelGroup = get(channelGroupName);
        
        final Member member = new Member(sessionId);
        
        /*
         * Add member to group.
         */
        if (channelGroup.getMembers().contains(member)) {
            throw new MirrorCacheException(Reason.CHANNELGROUP_ALREADY_JOINED)
                                                 .withDetail("member: " + member)
                                                 .withDetail("channelGroupName: " + channelGroupName);
        }
        channelGroup.addMember(new Member(sessionId));
    }
    
    /** Remove this sessionId from the memberList for this channelGroup. */
    public void channelGroupLeave(String sessionId, String channelGroupName) throws MirrorCacheException {
        final ServerChannelGroup channelGroup = get(channelGroupName);
        
        final Member member = new Member(sessionId);
        
        /*
         * Remove member from group.
         */
        if (!channelGroup.getMembers().contains(member)) {
            throw new MirrorCacheException(Reason.CHANNELGROUP_MEMBER_NOT_FOUND)
                                                 .withDetail("member: " + member)
                                                 .withDetail("channelGroupName: " + channelGroupName);
        }
        channelGroup.removeMember(member);
        
        
    }
    
    public Set<Member> channelGroupPublish(String sessionId, String channelGroupName) throws MirrorCacheException {
        final Set<Member> results = new HashSet<>();
        
        final ServerChannelGroup channelGroup = get(channelGroupName);
        if (channelGroup.getChannels().size() > 0) {
            /*
             * Add all members of group.
             */
            results.addAll(channelGroup.getMembers());
            
            /*
             * Add all members of each channel in group.
             */
            for (ServerChannel channel : channelGroup.getChannels()) {
                results.addAll(channel.getMembers());
            }
            
            /*
             * Exclude the current session from results.
             */
            final Member currentMember = new Member(sessionId);
            results.remove(currentMember);
        }
        
        return results;
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    private ServerChannelGroup get(String channelGroupName) throws MirrorCacheException {
        final ServerChannelGroup channelGroup = channelGroupMap.get(channelGroupName);
        if (channelGroup == null) {
            throw new MirrorCacheException(Reason.CHANNELGROUP_DNE).withDetail("channelGroupName: " + channelGroupName);
        }
        return channelGroup;
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    public List<ChannelGroupInfo> statusChannelGroups() {
        final List<ChannelGroupInfo> status = new ArrayList<>();
        
        for (ServerChannelGroup channelGroup : channelGroupMap.values()) {
            final ChannelGroupInfo.Builder builder = ChannelGroupInfo.newBuilder().setName(channelGroup.getName());
            
            for (ServerChannel channel : channelGroup.getChannels()) {
                builder.addChannel(ChannelInfo.newBuilder()
                                               .setName(channel.getName())
                                               .setType(channel.getType().name())
                                               .setVisibility(channel.getVisibility().name()));
            }
            
            for (Member member : channelGroup.getMembers()) {
                builder.addMember(MemberInfo.newBuilder()
                                            .setSessionId(member.getSessionId()));
            }
            
            status.add(builder.build());
        }
        return status;
    }
}
