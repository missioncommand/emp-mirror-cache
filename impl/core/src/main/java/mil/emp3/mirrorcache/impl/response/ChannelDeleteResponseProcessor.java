package mil.emp3.mirrorcache.impl.response;

import org.cmapi.primitives.proto.CmapiProto.OneOfCommand;

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

        final OneOfCommand oneOfCommand = message.getOperation().as(OneOfCommand.class);
        
        final String channelName = oneOfCommand.getChannelDelete().getChannelName();
        final String payloadId   = oneOfCommand.getChannelDelete().getPayloadId();
        final String sourceId    = oneOfCommand.getChannelDelete().getSourceId();
        
        // do not trigger an event if we sourced it
        if (!getMessageDispatcher().getClientInfo().clientId().equals(sourceId)) {
            getMessageDispatcher().dispatchEvent(new ChannelDeletedEvent(channelName, payloadId, message));    
        }
    }
}
