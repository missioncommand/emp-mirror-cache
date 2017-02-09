package org.cmapi.mirrorcache.impl.request;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.MirrorCacheException.Reason;
import org.cmapi.mirrorcache.Priority;
import org.cmapi.mirrorcache.impl.MessageDispatcher;
import org.cmapi.primitives.proto.CmapiProto.DeleteChannelGroupCommand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand.CommandCase;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteChannelGroupRequestProcessor extends BaseRequestProcessor<Message, Void> {
    static final private Logger LOG = LoggerFactory.getLogger(DeleteChannelGroupRequestProcessor.class);
    
    final private MessageDispatcher dispatcher;
    
    public DeleteChannelGroupRequestProcessor(MessageDispatcher dispatcher) {
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
            
            final DeleteChannelGroupCommand command = resMessage.getCommand(CommandCase.DELETE_CHANNEL_GROUP);
            if (command.getStatus() == Status.SUCCESS) {
                return null;
                
            } else {
                throw new MirrorCacheException(Reason.DELETE_CHANNELGROUP_FAILURE).withDetail("channelGroupName: " + command.getChannelGroupName());
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.info(Thread.currentThread().getName() + " thread interrupted.");
        }
        
        return null;
    }
}