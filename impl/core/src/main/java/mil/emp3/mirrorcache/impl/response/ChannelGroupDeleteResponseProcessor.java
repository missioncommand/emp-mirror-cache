package mil.emp3.mirrorcache.impl.response;

import org.cmapi.primitives.proto.CmapiProto.OneOfOperation;

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

        final OneOfOperation oneOfOperation = message.getOperation().as(OneOfOperation.class);
        
        final String channelGroupName = oneOfOperation.getChannelGroupDelete().getChannelGroupName();
        final String payloadId        = oneOfOperation.getChannelDelete().getPayloadId();
        final String sourceId         = oneOfOperation.getChannelDelete().getSourceId();
        
        // do not trigger an event if we sourced it
        if (!getMessageDispatcher().getClientInfo().clientId().equals(sourceId)) {
            getMessageDispatcher().dispatchEvent(new ChannelGroupDeletedEvent(channelGroupName, payloadId, message));    
        }
    }
}
