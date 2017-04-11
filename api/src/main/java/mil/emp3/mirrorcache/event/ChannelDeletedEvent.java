package mil.emp3.mirrorcache.event;

import mil.emp3.mirrorcache.Message;

/**
 * For when data has been deleted from a channel.
 */
public class ChannelDeletedEvent extends ChannelEvent<ChannelEventHandler> {
    static final public Type<ChannelEventHandler> TYPE = new Type<ChannelEventHandler>();
    
    final private String payloadId;
    final private Message message;
    
    public ChannelDeletedEvent(String channelName, String payloadId, Message message) {
        super(channelName);
        this.payloadId = payloadId;
        this.message   = message;
    }
    
    public String getPayloadId() {
        return payloadId;
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
        handler.onChannelDeletedEvent(this);
    }
}
