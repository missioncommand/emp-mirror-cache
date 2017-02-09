package org.cmapi.mirrorcache.channel;

import java.util.Set;

public interface ChannelGroupCache {

    String getChannelGroupName();
    
    Set<Integer> getEntityIds();
}
