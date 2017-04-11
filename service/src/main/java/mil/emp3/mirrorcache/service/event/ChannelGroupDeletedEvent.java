package mil.emp3.mirrorcache.service.event;

import mil.emp3.mirrorcache.channel.ChannelGroup;

public class ChannelGroupDeletedEvent {

    final private ChannelGroup channelGroup;
    
    public ChannelGroupDeletedEvent(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }
    
    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }
}
