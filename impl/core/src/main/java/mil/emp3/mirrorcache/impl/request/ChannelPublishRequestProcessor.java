package mil.emp3.mirrorcache.impl.request;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MessageDispatcher;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.Priority;

public class ChannelPublishRequestProcessor extends BaseRequestProcessor<Message, Void> {
//    static final private Logger LOG = LoggerFactory.getLogger(ChannelPublishRequestProcessor.class);
    
    final private MessageDispatcher dispatcher;
    
    public ChannelPublishRequestProcessor(MessageDispatcher dispatcher) {
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
//                final ChannelPublishOperation operation = resMessage.getOperation().getChannelPublish();
//                if (!(operation.getStatus() == Status.SUCCESS)) {
//                    throw new MirrorCacheException(Reason.CHANNEL_PUBLISH_FAILURE);
//                }
//            
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            LOG.info(Thread.currentThread().getName() + " thread interrupted.");
//        }
        
        return null;
    }
}
