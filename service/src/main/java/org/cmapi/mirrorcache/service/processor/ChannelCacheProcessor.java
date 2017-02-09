package org.cmapi.mirrorcache.service.processor;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.cmapi.mirrorcache.MirrorCacheException.Reason;
import org.cmapi.mirrorcache.channel.ChannelCache;
import org.cmapi.mirrorcache.Priority;
import org.cmapi.mirrorcache.service.CacheManager;
import org.cmapi.mirrorcache.service.SessionManager;
import org.cmapi.mirrorcache.support.ProtoMessageEntry;
import org.cmapi.primitives.proto.CmapiProto.ChannelCacheCommand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand;
import org.cmapi.primitives.proto.CmapiProto.ProtoMessage;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;

@ApplicationScoped
public class ChannelCacheProcessor implements CommandProcessor {

    @Inject
    private Logger LOG;
    
    @Inject
    private CacheManager cacheManager;
    
    @Inject
    private SessionManager sessionManager;
    
    @Override
    public void process(String sessionId, ProtoMessage req) {
        final ChannelCacheCommand command = req.getCommand().getChannelCache();

        final ChannelCache channelCache = cacheManager.getChannelCache(sessionId, command.getChannelName());
        
        final ProtoMessage res = ProtoMessage.newBuilder(req)
                .setPriority(Priority.MEDIUM.getValue())
                .setCommand(OneOfCommand.newBuilder()
                                        .setChannelCache(ChannelCacheCommand.newBuilder(command)
                                                                            .setStatus(Status.SUCCESS)
                                                                            .addAllEntityId(channelCache.getEntityIds())))
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
