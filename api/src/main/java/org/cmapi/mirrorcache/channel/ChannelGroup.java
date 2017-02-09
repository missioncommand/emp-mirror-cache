package org.cmapi.mirrorcache.channel;

import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.event.ChannelGroupEvent;
import org.cmapi.mirrorcache.event.EventHandler;
import org.cmapi.mirrorcache.event.EventRegistration;


public interface ChannelGroup {
    
    boolean isJoined();
    
    /** Retrieves published entity information for this group. */
    ChannelGroupCache cache() throws MirrorCacheException;
    
    /** Retrieves all history information for this group. */
    ChannelGroupHistory history() throws MirrorCacheException;
    
    /** Publishes a message to all channels in this group. */
    void publish(String id, Object payload) throws MirrorCacheException;
    
    /** can only add an existing channel. */
    void addChannel(String channelName) throws MirrorCacheException;
    void removeChannel(String channelName) throws MirrorCacheException;

    void join() throws MirrorCacheException;
    void leave() throws MirrorCacheException;
    
    /** Register to receive channelGroup events. */
    <T extends EventHandler> EventRegistration on(ChannelGroupEvent.Type<T> eventType, T listener);
    
    String getName();
}
