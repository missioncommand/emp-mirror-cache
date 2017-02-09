package org.cmapi.mirrorcache.event;

/**
 * For when data has been published to a channelGroup.
 */
public class ChannelGroupPublishedEvent extends ChannelGroupEvent<ChannelGroupEventHandler> {
    static final public Type<ChannelGroupEventHandler> TYPE = new Type<ChannelGroupEventHandler>();
    
    public ChannelGroupPublishedEvent(String channelGroupName) {
        super(channelGroupName);
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
