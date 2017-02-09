package org.cmapi.mirrorcache.impl.request;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.MirrorCacheException.Reason;
import org.cmapi.mirrorcache.Priority;
import org.cmapi.mirrorcache.impl.MessageDispatcher;
import org.cmapi.primitives.proto.CmapiProto.ChannelCloseCommand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand.CommandCase;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelCloseRequestProcessor extends BaseRequestProcessor<Message, Void> {
    static final private Logger LOG = LoggerFactory.getLogger(ChannelCloseRequestProcessor.class);
    
    final private MessageDispatcher dispatcher;
    
    public ChannelCloseRequestProcessor(MessageDispatcher dispatcher) {
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
            
            final ChannelCloseCommand command = resMessage.getCommand(CommandCase.CHANNEL_CLOSE);
            if (!(command.getStatus() == Status.SUCCESS)) {
                throw new MirrorCacheException(Reason.CHANNEL_CLOSE_FAILURE);
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.info(Thread.currentThread().getName() + " thread interrupted.");
        }
        
        return null;
    }

}
