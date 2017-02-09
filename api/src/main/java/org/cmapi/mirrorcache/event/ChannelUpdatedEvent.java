package org.cmapi.mirrorcache.event;

/**
 * For when properties or attributes of a channel have changed.
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
