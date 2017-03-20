package mil.emp3.mirrorcache.impl.response;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MessageDispatcher;
import mil.emp3.mirrorcache.event.ChannelDeletedEvent;

public class ChannelDeleteResponseProcessor extends BaseResponseProcessor {
    
    public ChannelDeleteResponseProcessor(MessageDispatcher dispatcher) {
        super(dispatcher, ChannelDeleteResponseProcessor.class.getSimpleName(), 5);
    }

    @Override
    protected void onMessage(Message message) {
        message.setEventType(ChannelDeletedEvent.TYPE);

        final String channelName = message.getCommand().getChannelDelete().getChannelName();
        final String payloadId = message.getCommand().getChannelDelete().getPayloadId();
        
        getMessageDispatcher().dispatchEvent(new ChannelDeletedEvent(channelName, payloadId, message));
    }
}
