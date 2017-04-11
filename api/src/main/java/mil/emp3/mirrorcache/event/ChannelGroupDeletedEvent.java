package mil.emp3.mirrorcache.event;

import mil.emp3.mirrorcache.Message;

/**
 * For when data has been deleted from a channelGroup.
 */
public class ChannelGroupDeletedEvent extends ChannelGroupEvent<ChannelGroupEventHandler> {
    static final public Type<ChannelGroupEventHandler> TYPE = new Type<ChannelGroupEventHandler>();
    
    final private String payloadId;
    final private Message message;
    
    public ChannelGroupDeletedEvent(String channelGroupName, String payloadId, Message message) {
        super(channelGroupName);
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
    public Type<ChannelGroupEventHandler> getType() {
        return TYPE;
    }

    @Override
    public void dispatch(ChannelGroupEventHandler handler) {
        handler.onChannelGroupDeletedEvent(this);
    }
}
