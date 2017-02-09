package org.cmapi.mirrorcache.impl.channel;

import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.channel.ChannelGroup;
import org.cmapi.mirrorcache.channel.ChannelGroupCache;
import org.cmapi.mirrorcache.channel.ChannelGroupHistory;
import org.cmapi.mirrorcache.event.EventHandler;
import org.cmapi.mirrorcache.event.EventRegistration;
import org.cmapi.mirrorcache.event.MirrorCacheEvent;

public class ChannelGroupAdaptor implements ChannelGroup {

    final private String name;
    
    public ChannelGroupAdaptor(ChannelGroup channelGroup) {
        this.name = channelGroup.getName();
    }
    public ChannelGroupAdaptor(String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public <T extends EventHandler> EventRegistration on(MirrorCacheEvent.Type<T> eventType, T listener) {
        throw new IllegalStateException("not implemented");
    }
    
    @Override
    public boolean isJoined() {
        throw new IllegalStateException("not implemented");
    }
    
    @Override
    public void addChannel(String channelName) throws MirrorCacheException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void removeChannel(String channelName) throws MirrorCacheException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void join() throws MirrorCacheException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void leave() throws MirrorCacheException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void publish(String id, Object payload) throws MirrorCacheException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public ChannelGroupCache cache() throws MirrorCacheException {
        throw new IllegalStateException("not implemented");
    }
    
    @Override
    public ChannelGroupHistory history() throws MirrorCacheException {
        throw new IllegalStateException("not implemented");
    }
    
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChannelGroupAdaptor other = (ChannelGroupAdaptor) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
