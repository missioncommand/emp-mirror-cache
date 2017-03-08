package mil.emp3.mirrorcache.service.event;

import mil.emp3.mirrorcache.channel.Channel;

public class ChannelDeletedEvent {

    final private Channel channel;
    
    public ChannelDeletedEvent(Channel channel) {
        this.channel = channel;
    }
    
    public Channel getChannel() {
        return channel;
    }
}
