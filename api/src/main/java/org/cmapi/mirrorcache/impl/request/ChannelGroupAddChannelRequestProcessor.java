package org.cmapi.mirrorcache.impl.request;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.MirrorCacheException.Reason;
import org.cmapi.mirrorcache.Priority;
import org.cmapi.mirrorcache.impl.MessageDispatcher;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupAddChannelCommand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand.CommandCase;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelGroupAddChannelRequestProcessor extends BaseRequestProcessor<Message, Void> {
    static final private Logger LOG = LoggerFactory.getLogger(ChannelGroupAddChannelRequestProcessor.class);
    
    final private MessageDispatcher dispatcher;
    
    public ChannelGroupAddChannelRequestProcessor(MessageDispatcher dispatcher) {
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
            
            final ChannelGroupAddChannelCommand command = resMessage.getCommand(CommandCase.CHANNEL_GROUP_ADD_CHANNEL);
            if (!(command.getStatus() == Status.SUCCESS)) {
                throw new MirrorCacheException(Reason.CHANNELGROUP_ADD_CHANNEL_FAILURE).withDetail("channelGroupName: " + command.getChannelGroupName())
                                                                                       .withDetail("channelName: " + command.getChannelName());
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.info(Thread.currentThread().getName() + " thread interrupted.");
        }
        
        return null;
    }
}