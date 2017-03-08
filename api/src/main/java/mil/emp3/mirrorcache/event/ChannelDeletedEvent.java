package mil.emp3.mirrorcache.event;

/**
 * For when a channel has been deleted.
 */
public class ChannelDeletedEvent extends ChannelEvent<ChannelEventHandler> {
    static final public Type<ChannelEventHandler> TYPE = new Type<ChannelEventHandler>();
    
    public ChannelDeletedEvent(String channelName) {
        super(channelName);
    }
    
    @Override
    public Type<ChannelEventHandler> getType() {
        return TYPE;
    }

    @Override
    public void dispatch(ChannelEventHandler handler) {
        handler.onChannelDeletedEvent(this);
    }
}
