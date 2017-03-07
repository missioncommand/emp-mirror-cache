package mil.emp3.mirrorcache.impl.channel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import mil.emp3.mirrorcache.channel.ChannelCache;

public class ClientChannelCache implements ChannelCache {

    final private String channelName;
    final private Set<Integer> entityIds;
    
    public ClientChannelCache(String channelName, Set<Integer> entityIds) {
        this.channelName = channelName;
        this.entityIds   = new HashSet<>(entityIds);
    }
    
    @Override
    public String getChannelName() {
        return channelName;
    }

    @Override
    public Set<Integer> getEntityIds() {
        return Collections.unmodifiableSet(entityIds);
    }

}
