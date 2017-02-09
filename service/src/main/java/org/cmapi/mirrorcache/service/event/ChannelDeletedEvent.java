package org.cmapi.mirrorcache.service.event;

import org.cmapi.mirrorcache.channel.Channel;

public class ChannelDeletedEvent {

    final private Channel channel;
    
    public ChannelDeletedEvent(Channel channel) {
        this.channel = channel;
    }
    
    public Channel getChannel() {
        return channel;
    }
}
