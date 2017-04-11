package mil.emp3.mirrorcache.impl.response;

import org.cmapi.primitives.proto.CmapiProto.OneOfOperation;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MessageDispatcher;
import mil.emp3.mirrorcache.event.ChannelPublishedEvent;

public class ChannelPublishResponseProcessor extends BaseResponseProcessor {
    
    public ChannelPublishResponseProcessor(MessageDispatcher dispatcher) {
        super(dispatcher, ChannelPublishResponseProcessor.class.getSimpleName(), 5);
    }

    @Override
    protected void onMessage(Message message) {
        message.setEventType(ChannelPublishedEvent.TYPE);

        final String channelName = message.getOperation().as(OneOfOperation.class).getChannelPublish().getChannelName();
        
        getMessageDispatcher().dispatchEvent(new ChannelPublishedEvent(channelName, message));
    }
}
