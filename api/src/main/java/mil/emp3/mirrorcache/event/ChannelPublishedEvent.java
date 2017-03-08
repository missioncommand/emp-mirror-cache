package mil.emp3.mirrorcache.event;

/**
 * For when data has been published to a channel.
 */
public class ChannelPublishedEvent extends ChannelEvent<ChannelEventHandler> {
    static final public Type<ChannelEventHandler> TYPE = new Type<ChannelEventHandler>();
    
    public ChannelPublishedEvent(String channelName) {
        super(channelName);
    }
    
    @Override
    public Type<ChannelEventHandler> getType() {
        return TYPE;
    }

    @Override
    public void dispatch(ChannelEventHandler handler) {
        handler.onChannelPublishedEvent(this);
    }
}
