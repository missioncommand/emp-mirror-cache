package org.cmapi.mirrorcache.spi;

import org.cmapi.mirrorcache.channel.ChannelHandler;

public interface ChannelHandlerProvider {

    boolean canHandle(String channelName);
    
    ChannelHandler getChannelHandler();
}
