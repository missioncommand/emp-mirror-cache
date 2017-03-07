package mil.emp3.mirrorcache.test;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.channel.ChannelHandler;

public class PliChannelHandler implements ChannelHandler {

    @Override
    public void processMessage(Message message) {
        System.out.println("__processMessage()");
    }
}
