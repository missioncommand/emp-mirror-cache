package org.cmapi.mirrorcache.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.cmapi.mirrorcache.channel.ChannelGroupCache;

public class ServerChannelGroupCache implements ChannelGroupCache {
    
    final private String channelGroupName;
    final private Set<Integer> entityIds;
    
    public ServerChannelGroupCache(String channelGroupName) {
        this.channelGroupName = channelGroupName;
        this.entityIds        = new HashSet<>();
    }
    
    @Override
    public String getChannelGroupName() {
        return channelGroupName;
    }
    
    @Override
    public Set<Integer> getEntityIds() {
        return Collections.unmodifiableSet(entityIds);
    }
    
    public void addEntityId(Integer entityId) {
        entityIds.add(entityId);
    }
}
