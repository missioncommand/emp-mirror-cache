package mil.emp3.mirrorcache.event;

import mil.emp3.mirrorcache.Message;

/**
 * For when data has been published to a channel.
 */
public class ChannelPublishedEvent extends ChannelEvent<ChannelEventHandler> {
    static final public Type<ChannelEventHandler> TYPE = new Type<ChannelEventHandler>();
    
    final private Message message;
    
    public ChannelPublishedEvent(String channelName, Message message) {
        super(channelName);
        this.message = message;
    }
    
    public Message getMessage() {
        return message;
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
