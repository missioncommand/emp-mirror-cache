package org.cmapi.mirrorcache.impl.response;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.event.ChannelGroupPublishedEvent;
import org.cmapi.mirrorcache.impl.MessageDispatcher;

public class ChannelGroupPublishResponseProcessor extends BaseResponseProcessor {
    
    public ChannelGroupPublishResponseProcessor(MessageDispatcher dispatcher) {
        super(dispatcher, ChannelGroupPublishResponseProcessor.class.getSimpleName(), 5);
    }

    @Override
    protected void onMessage(Message message) {
        message.setEventType(ChannelGroupPublishedEvent.TYPE);

        final String channelGroupName = message.getCommand().getChannelGroupPublish().getChannelGroupName();
        
        getMessageDispatcher().dispatchEvent(new ChannelGroupPublishedEvent(channelGroupName));
    }
}
