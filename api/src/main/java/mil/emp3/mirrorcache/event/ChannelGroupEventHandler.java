package mil.emp3.mirrorcache.event;

public interface ChannelGroupEventHandler extends EventHandler {

    /** Triggered when data has been published to a channelGroup. */
    void onChannelGroupPublishedEvent(ChannelGroupPublishedEvent event);
    
    /** Triggered when data has been deleted from a channelGroup. */
    void onChannelGroupDeletedEvent(ChannelGroupDeletedEvent event);
    
    /** Triggered when data has been updated in a channelGroup. */
    void onChannelGroupUpdatedEvent(ChannelGroupUpdatedEvent event);
}
