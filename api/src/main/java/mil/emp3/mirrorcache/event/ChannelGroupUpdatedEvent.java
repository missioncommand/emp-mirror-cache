package mil.emp3.mirrorcache.event;

/**
 * For when data has been updated in a channelGroup.
 */
public class ChannelGroupUpdatedEvent extends ChannelGroupEvent<ChannelGroupEventHandler> {
    static final public Type<ChannelGroupEventHandler> TYPE = new Type<ChannelGroupEventHandler>();
    
    public ChannelGroupUpdatedEvent(String channelName) {
        super(channelName);
    }
    
    @Override
    public Type<ChannelGroupEventHandler> getType() {
        return TYPE;
    }

    @Override
    public void dispatch(ChannelGroupEventHandler handler) {
        handler.onChannelGroupUpdatedEvent(this);
    }
}
