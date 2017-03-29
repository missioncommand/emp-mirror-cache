package mil.emp3.mirrorcache.impl.request;

import org.cmapi.primitives.proto.CmapiProto.ChannelGroupRemoveChannelCommand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand.CommandCase;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MessageDispatcher;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Priority;

public class ChannelGroupRemoveChannelRequestProcessor extends BaseRequestProcessor<Message, Void> {
    static final private Logger LOG = LoggerFactory.getLogger(ChannelGroupRemoveChannelRequestProcessor.class);
    
    final private MessageDispatcher dispatcher;
    
    public ChannelGroupRemoveChannelRequestProcessor(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
    
    @Override
    public Void executeSync(Message reqMessage) throws MirrorCacheException {
        if (reqMessage == null) {
            throw new IllegalStateException("reqMessage == null");
        }

        dispatcher.dispatchMessage(reqMessage.setPriority(Priority.MEDIUM));
        
        try {
            final Message resMessage = dispatcher.awaitResponse(reqMessage);
            
            final ChannelGroupRemoveChannelCommand command = resMessage.getCommand(CommandCase.CHANNEL_GROUP_REMOVE_CHANNEL);
            if (!(command.getStatus() == Status.SUCCESS)) {
                throw new MirrorCacheException(Reason.CHANNELGROUP_REMOVE_CHANNEL_FAILURE).withDetail("channelGroupName: " + command.getChannelGroupName())
                                                                                          .withDetail("channelName: " + command.getChannelName());
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.info(Thread.currentThread().getName() + " thread interrupted.");
        }
        
        return null;
    }
}