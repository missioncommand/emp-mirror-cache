package org.cmapi.mirrorcache.test;

import org.cmapi.mirrorcache.channel.ChannelHandler;
import org.cmapi.mirrorcache.spi.ChannelHandlerProvider;

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
