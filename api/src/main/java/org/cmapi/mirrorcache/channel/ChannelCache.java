package org.cmapi.mirrorcache.channel;

import java.util.Set;

public interface ChannelCache {

    String getChannelName();
    
    Set<Integer> getEntityIds();
}
