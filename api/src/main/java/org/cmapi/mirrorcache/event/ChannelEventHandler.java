package org.cmapi.mirrorcache.event;

public interface ChannelEventHandler extends EventHandler {

    void onChannelPublishedEvent(ChannelPublishedEvent event);
    void onChannelDeletedEvent(ChannelDeletedEvent event);
    void onChannelUpdatedEvent(ChannelUpdatedEvent event);
}
