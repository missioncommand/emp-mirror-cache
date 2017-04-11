package mil.emp3.mirrorcache.service.processor;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.cmapi.primitives.proto.CmapiProto.ChannelGroupDeleteOperation;
import org.cmapi.primitives.proto.CmapiProto.OneOfOperation;
import org.cmapi.primitives.proto.CmapiProto.ProtoMessage;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;

import mil.emp3.mirrorcache.Member;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Priority;
import mil.emp3.mirrorcache.service.CacheManager;
import mil.emp3.mirrorcache.service.ChannelGroupManager;
import mil.emp3.mirrorcache.service.SessionManager;
import mil.emp3.mirrorcache.service.support.ProtoMessageEntry;

@ApplicationScoped
public class ChannelGroupDeleteProcessor implements OperationProcessor {

    @Inject
    private Logger LOG;
    
    @Inject
    private ChannelGroupManager channelGroupManager;
    
    @Inject
    private CacheManager cacheManager;
    
    @Inject
    private SessionManager sessionManager;
    
    
    @Override
    public void process(String sessionId, ProtoMessage req) {
        final ChannelGroupDeleteOperation operation = req.getOperation().getChannelGroupDelete();
    
        /*
         * Update entity cache.
         */
//        final EntityCache entityCache = cacheManager.getEntityCache();
//        final CachedEntity entity     = entityCache.update(req.getPayload());
        
        /*
         * Update channelGroup cache.
         */
//        cacheManager.addToChannelGroupCache(sessionId, operation.getChannelGroupName(), entity);
        
        /*
         * Update channel history.
         */
//            historyManager.logChannelGroupEntry(sessionId, operation.getChannelGroupName(), req.getOperation());
        
        final ProtoMessage res = ProtoMessage.newBuilder(req)
                                             .setPriority(Priority.LOW.getValue())
                                             .setOperation(OneOfOperation.newBuilder()
                                                                         .setChannelGroupDelete(ChannelGroupDeleteOperation.newBuilder(operation)
                                                                                                                           .setStatus(Status.SUCCESS)))
                                             .build();
        
        /*
         * Distribute deletion to the other participants of channelGroup.
         */
        try {
            final Set<Member> otherMembers = channelGroupManager.getMembers(sessionId, operation.getChannelGroupName());
            LOG.debug("distributing to: " + otherMembers);
            
            for (Member otherMember : otherMembers) {
                if (!sessionManager.getOutboundQueue(otherMember.getSessionId()).offer(new ProtoMessageEntry(res), 1, TimeUnit.SECONDS)) {
                    throw new RuntimeException(Reason.QUEUE_OFFER_TIMEOUT.getMsg() + ", sessionId: " + otherMember.getSessionId());
                }
            }
        } catch (MirrorCacheException e) {
            throw new RuntimeException("ERROR: " + e.getMessage(), e);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.warn(Thread.currentThread().getName() + " thread was interrupted.");
        }
        
        
        /*
         * Provide response back to invoker.
         */
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
