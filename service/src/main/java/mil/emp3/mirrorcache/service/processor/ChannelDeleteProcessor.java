package mil.emp3.mirrorcache.service.processor;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.cmapi.primitives.proto.CmapiProto.ChannelDeleteCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelPublishCommand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand;
import org.cmapi.primitives.proto.CmapiProto.ProtoMessage;
import org.cmapi.primitives.proto.CmapiProto.ProtoPayload;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;

import mil.emp3.mirrorcache.Member;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.impl.Utils;
import mil.emp3.mirrorcache.Priority;
import mil.emp3.mirrorcache.service.CacheManager;
import mil.emp3.mirrorcache.service.ChannelManager;
import mil.emp3.mirrorcache.service.SessionManager;
import mil.emp3.mirrorcache.service.cache.CachedEntity;
import mil.emp3.mirrorcache.service.cache.EntityCache;
import mil.emp3.mirrorcache.service.support.ProtoMessageEntry;

@ApplicationScoped
public class ChannelDeleteProcessor implements CommandProcessor {

    @Inject
    private Logger LOG;
    
    @Inject
    private ChannelManager channelManager;
    
    @Inject
    private CacheManager cacheManager;
    
    @Inject
    private SessionManager sessionManager;
    
    
    @Override
    public void process(String sessionId, ProtoMessage req) {
        final ChannelDeleteCommand command = req.getCommand().getChannelDelete();
    
        /*
         * Update entity cache.
         */
        //final EntityCache entityCache = cacheManager.getEntityCache();
        //final CachedEntity entity     = entityCache.update(req.getPayload());
        
        /*
         * Update channel cache.
         */
//        cacheManager.addToChannelCache(sessionId, command.getChannelName(), entity);
        
        /*
         * Update channel history.
         */
//            historyManager.logChannelEntry(sessionId, command.getChannelName(), req.getCommand());
        
        final ProtoMessage res = ProtoMessage.newBuilder(req)
                .setPriority(Priority.LOW.getValue())
                .setCommand(OneOfCommand.newBuilder()
                                        .setChannelDelete(ChannelDeleteCommand.newBuilder(command)
                                                                              .setSourceId(sessionId)
                                                                              .setStatus(Status.SUCCESS)))
                .build();
        
        /*
         * Distribute deletion to the other participants of channel.
         */
        try {
            LOG.debug("distribute: " + Utils.asString(res));
            for (Member otherMember : channelManager.getMembers(sessionId, command.getChannelName())) {
                LOG.debug("\t-> " + otherMember);
                
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
