package mil.emp3.mirrorcache.service.processor;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.cmapi.primitives.proto.CmapiProto.ChannelGroupCacheOperation;
import org.cmapi.primitives.proto.CmapiProto.OneOfOperation;
import org.cmapi.primitives.proto.CmapiProto.ProtoMessage;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;

import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Priority;
import mil.emp3.mirrorcache.channel.ChannelGroupCache;
import mil.emp3.mirrorcache.service.CacheManager;
import mil.emp3.mirrorcache.service.SessionManager;
import mil.emp3.mirrorcache.service.support.ProtoMessageEntry;

@ApplicationScoped
public class ChannelGroupCacheProcessor implements OperationProcessor {

    @Inject
    private Logger LOG;
    
    @Inject
    private CacheManager cacheManager;
    
    @Inject
    private SessionManager sessionManager;
    
    @Override
    public void process(String sessionId, ProtoMessage req) {
        final ChannelGroupCacheOperation operation = req.getOperation().getChannelGroupCache();

        final ChannelGroupCache channelGroupCache = cacheManager.getChannelGroupCache(sessionId, operation.getChannelGroupName());
        
        final ProtoMessage res = ProtoMessage.newBuilder(req)
                                             .setPriority(Priority.MEDIUM.getValue())
                                             .setOperation(OneOfOperation.newBuilder()
                                                                         .setChannelGroupCache(ChannelGroupCacheOperation.newBuilder(operation)
                                                                                                                         .setStatus(Status.SUCCESS)
                                                                                                                         .addAllEntityId(channelGroupCache.getEntityIds())))
                                             .build();
        
        try {
            if (!sessionManager.getOutboundQueue(sessionId).offer(new ProtoMessageEntry(res), 1, TimeUnit.SECONDS)) {
                throw new RuntimeException(Reason.QUEUE_OFFER_TIMEOUT.getMsg() + ", sessionId: " + sessionId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.warn(Thread.currentThread().getName() + " thread was interrupted.");
        }
    }
}
