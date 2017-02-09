package org.cmapi.mirrorcache.event;

/**
 * For when a channelGroup has been deleted.
 */
public class ChannelGroupDeletedEvent extends ChannelGroupEvent<ChannelGroupEventHandler> {
    static final public Type<ChannelGroupEventHandler> TYPE = new Type<ChannelGroupEventHandler>();
    
    public ChannelGroupDeletedEvent(String channelName) {
        super(channelName);
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
