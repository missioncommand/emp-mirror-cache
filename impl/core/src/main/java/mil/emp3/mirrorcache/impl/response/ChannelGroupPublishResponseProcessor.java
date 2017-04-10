package mil.emp3.mirrorcache.impl.response;

import org.cmapi.primitives.proto.CmapiProto.OneOfOperation;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MessageDispatcher;
import mil.emp3.mirrorcache.event.ChannelGroupPublishedEvent;

public class ChannelGroupPublishResponseProcessor extends BaseResponseProcessor {
    
    public ChannelGroupPublishResponseProcessor(MessageDispatcher dispatcher) {
        super(dispatcher, ChannelGroupPublishResponseProcessor.class.getSimpleName(), 5);
    }

    @Override
    protected void onMessage(Message message) {
        message.setEventType(ChannelGroupPublishedEvent.TYPE);

        final String channelGroupName = message.getOperation().as(OneOfOperation.class).getChannelGroupPublish().getChannelGroupName();

        getMessageDispatcher().dispatchEvent(new ChannelGroupPublishedEvent(channelGroupName, message));
    }
}
