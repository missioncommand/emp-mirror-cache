package mil.emp3.mirrorcache.impl.response;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MessageDispatcher;
import mil.emp3.mirrorcache.event.ChannelGroupDeletedEvent;

public class ChannelGroupDeleteResponseProcessor extends BaseResponseProcessor {
    
    public ChannelGroupDeleteResponseProcessor(MessageDispatcher dispatcher) {
        super(dispatcher, ChannelGroupDeleteResponseProcessor.class.getSimpleName(), 5);
    }

    @Override
    protected void onMessage(Message message) {
        message.setEventType(ChannelGroupDeletedEvent.TYPE);

        final String channelGroupName = message.getCommand().getChannelGroupDelete().getChannelGroupName();
        final String payloadId = message.getCommand().getChannelDelete().getPayloadId();
        
        getMessageDispatcher().dispatchEvent(new ChannelGroupDeletedEvent(channelGroupName, payloadId, message));
    }
}
