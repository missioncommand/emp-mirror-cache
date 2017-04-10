package mil.emp3.mirrorcache.impl.request;

import java.util.ArrayList;
import java.util.List;

import org.cmapi.primitives.proto.CmapiProto.ChannelInfo;
import org.cmapi.primitives.proto.CmapiProto.FindChannelsOperation;
import org.cmapi.primitives.proto.CmapiProto.OneOfOperation;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MessageDispatcher;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Priority;
import mil.emp3.mirrorcache.channel.Channel;
import mil.emp3.mirrorcache.channel.Channel.Type;
import mil.emp3.mirrorcache.channel.Channel.Visibility;
import mil.emp3.mirrorcache.impl.channel.ClientChannel;

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
            
            final FindChannelsOperation operation = resMessage.getOperation().as(OneOfOperation.class).getFindChannels();
            if (operation.getStatus() == Status.SUCCESS) {
                
                final List<Channel> results = new ArrayList<>();

                for (ChannelInfo channel : operation.getChannelList()) {
                    results.add(new ClientChannel(channel.getName(),
                                                  Visibility.valueOf(channel.getVisibility()),
                                                  Type.valueOf(channel.getType()),
                                                  channel.getIsOpen(),
                                                  dispatcher));
                }
                return results;
                
            } else {
                throw new MirrorCacheException(Reason.FIND_CHANNELS_FAILURE).withDetail("filter: " + operation.getFilter());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.info(Thread.currentThread().getName() + " thread interrupted.");
        }
        
        throw new IllegalStateException(new MirrorCacheException(Reason.UNKNOWN));
    }

}
