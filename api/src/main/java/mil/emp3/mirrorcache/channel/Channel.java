package mil.emp3.mirrorcache.channel;

import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.event.ChannelEvent;
import mil.emp3.mirrorcache.event.EventHandler;
import mil.emp3.mirrorcache.event.EventRegistration;

public interface Channel {

    public enum Type {
        /** A temporary channel is shutdown when the owner's session is closed */
        TEMPORARY,
        PERSISTENT,
        ;
    }
    public enum Visibility {
        PRIVATE,
        PUBLIC,
        ;
    }
    
    public enum Flow {
        /** Client can only consume messages */
        INGRESS, 
        /** Client can only produce messages */
        EGRESS, 
        /** Client can consume and produce messages */
        BOTH,
        ;
    }

    /** The return value is from the perspective of the client invoking it. */
    boolean isOpen();
    
    /** Retrieves published entity information for this channel. */
    ChannelCache cache() throws MirrorCacheException;
    
    /** Retrieves all history information for this channel. */
    ChannelHistory history() throws MirrorCacheException;
    
    void publish(String id, Class<?> type, Object payload) throws MirrorCacheException;
    
    /** Opening a channel will allow producing or consuming from it. */
    void open(Flow flow, String filter) throws MirrorCacheException;
    void close() throws MirrorCacheException;
    
    /** Register to receive channel events. */
    <T extends EventHandler> EventRegistration on(ChannelEvent.Type<T> eventType, T listener);
    
    String getName();
    Type getType();
    Visibility getVisibility();
    
}
