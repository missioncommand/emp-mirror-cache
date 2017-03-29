package mil.emp3.mirrorcache.channel;

import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.event.ChannelGroupEvent;
import mil.emp3.mirrorcache.event.EventHandler;
import mil.emp3.mirrorcache.event.EventRegistration;


public interface ChannelGroup {
    
    boolean isOpen();
    
    /** Retrieves published entity information for this group. */
    ChannelGroupCache cache() throws MirrorCacheException;
    
    /** Retrieves all history information for this group. */
    ChannelGroupHistory history() throws MirrorCacheException;
    
    /** Publishes a message to all channels in this group. */
    void publish(String id, Class<?> type, Object payload) throws MirrorCacheException;
    void delete(String id) throws MirrorCacheException;

    void open() throws MirrorCacheException;
    void close() throws MirrorCacheException;
    
    /** Register to receive channelGroup events. */
    <T extends EventHandler> EventRegistration on(ChannelGroupEvent.Type<T> eventType, T listener);
    
    String getName();
    
    /** can only add an existing channel. */
    void addChannel(String channelName) throws MirrorCacheException;
    void removeChannel(String channelName) throws MirrorCacheException;
}
