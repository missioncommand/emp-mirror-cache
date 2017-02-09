package org.cmapi.mirrorcache.impl.request;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.Priority;
import org.cmapi.mirrorcache.impl.MessageDispatcher;

public class ChannelGroupPublishRequestProcessor extends BaseRequestProcessor<Message, Void> {
//    static final private Logger LOG = LoggerFactory.getLogger(ChannelGroupPublishRequestProcessor.class);
    
    final private MessageDispatcher dispatcher;
    
    public ChannelGroupPublishRequestProcessor(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
    
    @Override
    public Void executeSync(Message reqMessage) throws MirrorCacheException {
        if (reqMessage == null) {
            throw new IllegalStateException("reqMessage == null");
        }

        dispatcher.dispatchMessage(reqMessage.setPriority(Priority.LOW));
        
//        try {
//            final Message resMessage = dispatcher.awaitResponse(reqMessage);
//            
//                final ChannelGroupPublishCommand command = resMessage.getCommand(CommandCase.CHANNEL_GROUP_PUBLISH);
//                if (!(command.getStatus() == Status.SUCCESS)) {
//                    throw new MirrorCacheException(Reason.CHANNELGROUP_PUBLISH_FAILURE);
//                }
//            
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            LOG.info(Thread.currentThread().getName() + " thread interrupted.");
//        }
        
        return null;
    }
}
