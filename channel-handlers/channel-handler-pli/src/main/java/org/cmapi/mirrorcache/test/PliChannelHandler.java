package org.cmapi.mirrorcache.test;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.channel.ChannelHandler;

public class PliChannelHandler implements ChannelHandler {

    @Override
    public void processMessage(Message message) {
        System.out.println("__processMessage()");
    }
}
