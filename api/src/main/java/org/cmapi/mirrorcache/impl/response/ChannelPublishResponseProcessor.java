package org.cmapi.mirrorcache.impl.response;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.event.ChannelPublishedEvent;
import org.cmapi.mirrorcache.impl.MessageDispatcher;

public class ChannelPublishResponseProcessor extends BaseResponseProcessor {
    
    public ChannelPublishResponseProcessor(MessageDispatcher dispatcher) {
        super(dispatcher, ChannelPublishResponseProcessor.class.getSimpleName(), 5);
    }

    @Override
    protected void onMessage(Message message) {
        message.setEventType(ChannelPublishedEvent.TYPE);

        final String channelName = message.getCommand().getChannelPublish().getChannelName();
        
        getMessageDispatcher().dispatchEvent(new ChannelPublishedEvent(channelName));
    }
}
