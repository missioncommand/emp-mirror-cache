package org.cmapi.mirrorcache.impl.request;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.MirrorCacheException.Reason;
import org.cmapi.mirrorcache.channel.ChannelGroup;
import org.cmapi.mirrorcache.Priority;
import org.cmapi.mirrorcache.impl.MessageDispatcher;
import org.cmapi.mirrorcache.impl.channel.ClientChannelGroup;
import org.cmapi.primitives.proto.CmapiProto.CreateChannelGroupCommand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand.CommandCase;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateChannelGroupRequestProcessor extends BaseRequestProcessor<Message, ChannelGroup> {
    static final private Logger LOG = LoggerFactory.getLogger(CreateChannelRequestProcessor.class);
    
    final private MessageDispatcher dispatcher;
    
    public CreateChannelGroupRequestProcessor(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public ChannelGroup executeSync(Message reqMessage) throws MirrorCacheException {
        if (reqMessage == null) {
            throw new IllegalStateException("reqMessage == null");
        }
        
        dispatcher.dispatchMessage(reqMessage.setPriority(Priority.MEDIUM));
        
        try {
            final Message resMessage = dispatcher.awaitResponse(reqMessage);
            
            final CreateChannelGroupCommand command = resMessage.getCommand(CommandCase.CREATE_CHANNEL_GROUP);
            if (command.getStatus() == Status.SUCCESS) {
            
                final ChannelGroup channelGroup = new ClientChannelGroup(command.getChannelGroupName(), false, dispatcher);
                return channelGroup;
                
            } else {
                throw new MirrorCacheException(Reason.CREATE_CHANNELGROUP_FAILURE).withDetail("channelGroupName: " + command.getChannelGroupName());
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.info(Thread.currentThread().getName() + " thread interrupted.");
        }
        
        throw new IllegalStateException(new MirrorCacheException(Reason.UNKNOWN));
    }
}
