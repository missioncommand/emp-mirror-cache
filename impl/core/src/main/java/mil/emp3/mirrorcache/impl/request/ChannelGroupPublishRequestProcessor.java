package mil.emp3.mirrorcache.impl.request;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MessageDispatcher;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.Priority;

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
//                final ChannelGroupPublishOperation operation = resMessage.getOperation().getChannelGroupPublish();
//                if (!(operation.getStatus() == Status.SUCCESS)) {
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
