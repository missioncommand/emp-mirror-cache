package org.cmapi.mirrorcache.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.cmapi.mirrorcache.Member;
import org.cmapi.mirrorcache.MirrorCacheException.Reason;
import org.cmapi.mirrorcache.impl.channel.ChannelGroupAdaptor;

public class ServerChannelGroup extends ChannelGroupAdaptor {

    final private String ownerId;
    final private Set<ServerChannel> channels;
    final private Set<Member> members;
    
    public ServerChannelGroup(String ownerId, String channelGroupName) {
        super(channelGroupName);
        
        this.channels = new HashSet<>();
        this.members  = new HashSet<>();
        this.ownerId  = ownerId;
    }
    
    public String getOwnerId() {
        return ownerId;
    }
    
    public Set<ServerChannel> getChannels() {
        return Collections.unmodifiableSet(channels);
    }
    public Set<Member> getMembers() {
        return Collections.unmodifiableSet(members);
    }
    
    public void addMember(Member member) {
        if (!members.add(member)) {
            throw new IllegalStateException(Reason.CHANNELGROUP_MEMBER_ALREADY_EXISTS.getMsg());
        }
    }
    public void removeMember(Member member) {
        if (!members.remove(member)) {
            throw new IllegalStateException(Reason.CHANNELGROUP_MEMBER_NOT_FOUND.getMsg());
        }
    }
    
    public void addChannel(ServerChannel channel) {
        if (!channels.add(channel)) {
            throw new IllegalStateException(Reason.CHANNELGROUP_CHANNEL_ALREADY_EXISTS.getMsg());
        }
    }
    public void removeChannel(ServerChannel channel) {
        if (!channels.remove(channel)) {
            throw new IllegalStateException(Reason.CHANNELGROUP_CHANNEL_NOT_FOUND.getMsg());
        }
    }
    
    /** Determine if this channelGroup should be considered joined to this member. */
    public boolean isJoined(Member member) {
        return members.contains(member);
    }
}
