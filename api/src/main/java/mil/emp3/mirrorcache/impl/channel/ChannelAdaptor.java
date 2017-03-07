package mil.emp3.mirrorcache.impl.channel;

import mil.emp3.mirrorcache.channel.Channel;
import mil.emp3.mirrorcache.channel.ChannelCache;
import mil.emp3.mirrorcache.channel.ChannelHistory;
import mil.emp3.mirrorcache.event.EventHandler;
import mil.emp3.mirrorcache.event.EventRegistration;
import mil.emp3.mirrorcache.event.MirrorCacheEvent;

public class ChannelAdaptor implements Channel {

    final private String name;
    final private Visibility visibility;
    final private Type type;
    
    public ChannelAdaptor(Channel channel) {
        this.name       = channel.getName();
        this.visibility = channel.getVisibility();
        this.type       = channel.getType();
    }
    public ChannelAdaptor(String name, Visibility visibility, Type type) {
        this.name       = name;
        this.visibility = visibility;
        this.type       = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Visibility getVisibility() {
        return visibility;
    }

    @Override
    public <T extends EventHandler> EventRegistration on(MirrorCacheEvent.Type<T> eventType, T listener) {
        throw new IllegalStateException("not implemented");
    }
    
    @Override
    public boolean isOpen() {
        throw new IllegalStateException("not implemented");
    }
    
    @Override
    public void publish(String id, Object payload) {
        throw new IllegalStateException("not implemented");
    }
    
    @Override
    public ChannelCache cache() {
        throw new IllegalStateException("not implemented");
    }
    
    @Override
    public ChannelHistory history() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void open(Flow flow, String filter) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void close() {
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
        ChannelAdaptor other = (ChannelAdaptor) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    
}
