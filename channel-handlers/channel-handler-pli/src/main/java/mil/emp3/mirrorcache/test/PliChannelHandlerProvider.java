package mil.emp3.mirrorcache.test;

import mil.emp3.mirrorcache.channel.ChannelHandler;
import mil.emp3.mirrorcache.spi.ChannelHandlerProvider;

public class PliChannelHandlerProvider implements ChannelHandlerProvider {

    final private ChannelHandler channelHandler = new PliChannelHandler();
    
    @Override
    public boolean canHandle(String channelName) {
        return true;
    }

    @Override
    public ChannelHandler getChannelHandler() {
        return channelHandler;
    }

}
