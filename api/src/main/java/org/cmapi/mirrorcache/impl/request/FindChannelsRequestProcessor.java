package org.cmapi.mirrorcache.impl.request;

import java.util.ArrayList;
import java.util.List;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.MirrorCacheException.Reason;
import org.cmapi.mirrorcache.channel.Channel;
import org.cmapi.mirrorcache.channel.Channel.Type;
import org.cmapi.mirrorcache.channel.Channel.Visibility;
import org.cmapi.mirrorcache.Priority;
import org.cmapi.mirrorcache.impl.MessageDispatcher;
import org.cmapi.mirrorcache.impl.channel.ClientChannel;
import org.cmapi.primitives.proto.CmapiProto.ChannelInfo;
import org.cmapi.primitives.proto.CmapiProto.FindChannelsCommand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand.CommandCase;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindChannelsRequestProcessor extends BaseRequestProcessor<Message, List<Channel>> {
    static final private Logger LOG = LoggerFactory.getLogger(FindChannelsRequestProcessor.class);
    
    final private MessageDispatcher dispatcher;
    
    public FindChannelsRequestProcessor(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
    
    @Override
    public List<Channel> executeSync(Message reqMessage) throws MirrorCacheException {
        if (reqMessage == null) {
            throw new IllegalStateException("reqMessage == null");
        }
        
        dispatcher.dispatchMessage(reqMessage.setPriority(Priority.MEDIUM));
        
        try {
            final Message resMessage = dispatcher.awaitResponse(reqMessage);
            
            final FindChannelsCommand command = resMessage.getCommand(CommandCase.FIND_CHANNELS);
            if (command.getStatus() == Status.SUCCESS) {
                
                final List<Channel> results = new ArrayList<>();

                for (ChannelInfo channel : command.getChannelList()) {
                    results.add(new ClientChannel(channel.getName(),
                                                  Visibility.valueOf(channel.getVisibility()),
                                                  Type.valueOf(channel.getType()),
                                                  channel.getIsOpen(),
                                                  dispatcher));
                }
                return results;
                
            } else {
                throw new MirrorCacheException(Reason.FIND_CHANNELS_FAILURE).withDetail("filter: " + command.getFilter());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.info(Thread.currentThread().getName() + " thread interrupted.");
        }
        
        throw new IllegalStateException(new MirrorCacheException(Reason.UNKNOWN));
    }

}
