package org.cmapi.mirrorcache.service.event;

import org.cmapi.mirrorcache.channel.ChannelGroup;

public class ChannelGroupDeletedEvent {

    final private ChannelGroup channelGroup;
    
    public ChannelGroupDeletedEvent(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }
    
    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }
}
