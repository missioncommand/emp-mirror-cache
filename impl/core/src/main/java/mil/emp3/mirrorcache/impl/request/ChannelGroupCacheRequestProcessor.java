package mil.emp3.mirrorcache.impl.request;

import java.util.HashSet;

import org.cmapi.primitives.proto.CmapiProto.ChannelGroupCacheOperation;
import org.cmapi.primitives.proto.CmapiProto.OneOfOperation;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MessageDispatcher;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Priority;
import mil.emp3.mirrorcache.channel.ChannelGroupCache;
import mil.emp3.mirrorcache.impl.channel.ClientChannelGroupCache;

public class ChannelGroupCacheRequestProcessor extends BaseRequestProcessor<Message, ChannelGroupCache> {
    static final private Logger LOG = LoggerFactory.getLogger(ChannelGroupCacheRequestProcessor.class);
    
    final private MessageDispatcher dispatcher;
    
    public ChannelGroupCacheRequestProcessor(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
    
    @Override
    public ChannelGroupCache executeSync(Message reqMessage) throws MirrorCacheException {
        if (reqMessage == null) {
            throw new IllegalStateException("reqMessage == null");
        }

        dispatcher.dispatchMessage(reqMessage.setPriority(Priority.MEDIUM));
        
        try {
            final Message resMessage = dispatcher.awaitResponse(reqMessage);
            
            final ChannelGroupCacheOperation operation = resMessage.getOperation().as(OneOfOperation.class).getChannelGroupCache();
            if (operation.getStatus() == Status.SUCCESS) {
                
                final ChannelGroupCache cache = new ClientChannelGroupCache(operation.getChannelGroupName(), new HashSet<>(operation.getEntityIdList()));
                return cache;
                
            } else {
                throw new MirrorCacheException(Reason.CHANNELGROUP_CACHE_FAILURE).withDetail("channelGroupName: " + operation.getChannelGroupName());
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.info(Thread.currentThread().getName() + " thread interrupted.");
        }
        
        throw new IllegalStateException(new MirrorCacheException(Reason.UNKNOWN));
    }
}
