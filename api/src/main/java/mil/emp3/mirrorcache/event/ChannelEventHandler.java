package mil.emp3.mirrorcache.event;

public interface ChannelEventHandler extends EventHandler {

    /** Triggered when data has been published to a channel. */
    void onChannelPublishedEvent(ChannelPublishedEvent event);
    
    /** Triggered when data has been deleted from a channel. */
    void onChannelDeletedEvent(ChannelDeletedEvent event);
    
    /** Triggered when data has been updated in a channel. */
    void onChannelUpdatedEvent(ChannelUpdatedEvent event);
}
