package org.cmapi.mirrorcache.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.cmapi.mirrorcache.Member;
import org.cmapi.mirrorcache.MirrorCacheException.Reason;
import org.cmapi.mirrorcache.impl.channel.ChannelAdaptor;

public class ServerChannel extends ChannelAdaptor {
    
    final private String ownerId;
    final private Set<Member> members;
    
    public ServerChannel(String ownerId, String channelName, Visibility visibility, Type type) {
        super(channelName, visibility, type);
        
        this.members = new HashSet<>();
        this.ownerId = ownerId;
    }
    
    public String getOwnerId() {
        return ownerId;
    }
    
    public Set<Member> getMembers() {
        return Collections.unmodifiableSet(members);
    }
    
    public void addMember(Member member) {
        if (!members.add(member)) {
            throw new IllegalStateException(Reason.CHANNEL_MEMBER_ALREADY_EXISTS.getMsg());
        }
    }
    public void removeMember(Member member) {
        if (!members.remove(member)) {
            throw new IllegalStateException(Reason.CHANNEL_MEMBER_NOT_FOUND.getMsg());
        }
    }
    
    /** Determine if this channel should be considered open to this member. */
    public boolean isOpen(Member member) {
        return members.contains(member);
    }
}
