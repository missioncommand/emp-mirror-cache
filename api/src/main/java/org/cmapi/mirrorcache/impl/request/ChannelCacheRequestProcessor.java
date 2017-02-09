package org.cmapi.mirrorcache.impl.request;

import java.util.HashSet;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.MirrorCacheException.Reason;
import org.cmapi.mirrorcache.channel.ChannelCache;
import org.cmapi.mirrorcache.Priority;
import org.cmapi.mirrorcache.impl.MessageDispatcher;
import org.cmapi.mirrorcache.impl.channel.ClientChannelCache;
import org.cmapi.primitives.proto.CmapiProto.ChannelCacheCommand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand.CommandCase;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelCacheRequestProcessor extends BaseRequestProcessor<Message, ChannelCache> {
    static final private Logger LOG = LoggerFactory.getLogger(ChannelCacheRequestProcessor.class);
    
    final private MessageDispatcher dispatcher;
    
    public ChannelCacheRequestProcessor(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
    
    @Override
    public ChannelCache executeSync(Message reqMessage) throws MirrorCacheException {
        if (reqMessage == null) {
            throw new IllegalStateException("reqMessage == null");
        }

        dispatcher.dispatchMessage(reqMessage.setPriority(Priority.MEDIUM));
        
        try {
            final Message resMessage = dispatcher.awaitResponse(reqMessage);
            
            final ChannelCacheCommand command = resMessage.getCommand(CommandCase.CHANNEL_CACHE);
            if (command.getStatus() == Status.SUCCESS) {
                
                final ChannelCache cache = new ClientChannelCache(command.getChannelName(), new HashSet<>(command.getEntityIdList()));
                return cache;
                
            } else {
                throw new MirrorCacheException(Reason.CHANNEL_CACHE_FAILURE).withDetail("channelName: " + command.getChannelName());
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.info(Thread.currentThread().getName() + " thread interrupted.");
        }
        
        throw new IllegalStateException(new MirrorCacheException(Reason.UNKNOWN));
    }
}
