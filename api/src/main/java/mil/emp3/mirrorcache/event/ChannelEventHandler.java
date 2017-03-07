package mil.emp3.mirrorcache.event;

public interface ChannelEventHandler extends EventHandler {

    void onChannelPublishedEvent(ChannelPublishedEvent event);
    void onChannelDeletedEvent(ChannelDeletedEvent event);
    void onChannelUpdatedEvent(ChannelUpdatedEvent event);
}
