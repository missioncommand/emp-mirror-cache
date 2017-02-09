package org.cmapi.mirrorcache.event;

public interface ChannelGroupEventHandler extends EventHandler {

    void onChannelGroupPublishedEvent(ChannelGroupPublishedEvent event);
    void onChannelGroupDeletedEvent(ChannelGroupDeletedEvent event);
    void onChannelGroupUpdatedEvent(ChannelGroupUpdatedEvent event);
}
