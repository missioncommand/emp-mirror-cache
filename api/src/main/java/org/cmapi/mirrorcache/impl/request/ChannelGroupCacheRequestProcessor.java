package org.cmapi.mirrorcache.impl.request;

import java.util.HashSet;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.MirrorCacheException.Reason;
import org.cmapi.mirrorcache.channel.ChannelGroupCache;
import org.cmapi.mirrorcache.Priority;
import org.cmapi.mirrorcache.impl.MessageDispatcher;
import org.cmapi.mirrorcache.impl.channel.ClientChannelGroupCache;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupCacheCommand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand.CommandCase;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            
            final ChannelGroupCacheCommand command = resMessage.getCommand(CommandCase.CHANNEL_GROUP_CACHE);
            if (command.getStatus() == Status.SUCCESS) {
                
                final ChannelGroupCache cache = new ClientChannelGroupCache(command.getChannelGroupName(), new HashSet<>(command.getEntityIdList()));
                return cache;
                
            } else {
                throw new MirrorCacheException(Reason.CHANNELGROUP_CACHE_FAILURE).withDetail("channelGroupName: " + command.getChannelGroupName());
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.info(Thread.currentThread().getName() + " thread interrupted.");
        }
        
        throw new IllegalStateException(new MirrorCacheException(Reason.UNKNOWN));
    }
}
