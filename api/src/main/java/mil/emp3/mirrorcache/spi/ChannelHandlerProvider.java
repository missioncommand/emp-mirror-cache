package mil.emp3.mirrorcache.spi;

import mil.emp3.mirrorcache.channel.ChannelHandler;

public interface ChannelHandlerProvider {

    boolean canHandle(String channelName);
    
    ChannelHandler getChannelHandler();
}
