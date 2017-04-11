package mil.emp3.mirrorcache.event;

/**
 * For when data has been updated in a channel.
 */
public class ChannelUpdatedEvent extends ChannelEvent<ChannelEventHandler> {
    static final public Type<ChannelEventHandler> TYPE = new Type<ChannelEventHandler>();
    
    public ChannelUpdatedEvent(String channelName) {
        super(channelName);
    }
    
    @Override
    public Type<ChannelEventHandler> getType() {
        return TYPE;
    }

    @Override
    public void dispatch(ChannelEventHandler handler) {
        handler.onChannelUpdatedEvent(this);
    }
}
