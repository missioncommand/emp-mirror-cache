package org.cmapi.mirrorcache.impl.channel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.cmapi.mirrorcache.channel.ChannelGroupCache;

public class ClientChannelGroupCache implements ChannelGroupCache {

    final private String channelGroupName;
    final private Set<Integer> entityIds;
    
    public ClientChannelGroupCache(String channelGroupName, Set<Integer> entityIds) {
        this.channelGroupName = channelGroupName;
        this.entityIds        = new HashSet<>(entityIds);
    }
    
    @Override
    public String getChannelGroupName() {
        return channelGroupName;
    }

    @Override
    public Set<Integer> getEntityIds() {
        return Collections.unmodifiableSet(entityIds);
    }

}
