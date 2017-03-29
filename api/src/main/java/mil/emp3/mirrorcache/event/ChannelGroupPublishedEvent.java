package mil.emp3.mirrorcache.event;

import mil.emp3.mirrorcache.Message;

/**
 * For when data has been published to a channelGroup.
 */
public class ChannelGroupPublishedEvent extends ChannelGroupEvent<ChannelGroupEventHandler> {
    static final public Type<ChannelGroupEventHandler> TYPE = new Type<ChannelGroupEventHandler>();
    
    final private Message message;
    
    public ChannelGroupPublishedEvent(String channelGroupName, Message message) {
        super(channelGroupName);
        this.message = message;
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
        handler.onChannelGroupPublishedEvent(this);
    }
}
