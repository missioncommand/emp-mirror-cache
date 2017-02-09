package org.cmapi.mirrorcache.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.cmapi.mirrorcache.channel.ChannelCache;

public class ServerChannelCache implements ChannelCache {
    
    final private String channelName;
    final private Set<Integer> entityIds;
    
    public ServerChannelCache(String channelName) {
        this.channelName = channelName;
        this.entityIds   = new HashSet<>();
    }
    
    @Override
    public String getChannelName() {
        return channelName;
    }
    
    @Override
    public Set<Integer> getEntityIds() {
        return Collections.unmodifiableSet(entityIds);
    }
    
    public void addEntityId(Integer entityId) {
        entityIds.add(entityId);
    }
}
