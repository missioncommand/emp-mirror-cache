package mil.emp3.mirrorcache.impl.request;

import org.cmapi.primitives.proto.CmapiProto.CreateChannelGroupCommand;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MessageDispatcher;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Priority;
import mil.emp3.mirrorcache.channel.ChannelGroup;
import mil.emp3.mirrorcache.impl.channel.ClientChannelGroup;

public class CreateChannelGroupRequestProcessor extends BaseRequestProcessor<Message, ChannelGroup> {
    static final private Logger LOG = LoggerFactory.getLogger(CreateChannelRequestProcessor.class);
    
    final private MessageDispatcher dispatcher;
    
    public CreateChannelGroupRequestProcessor(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public ChannelGroup executeSync(Message reqMessage) throws MirrorCacheException {
        if (reqMessage == null) {
            throw new IllegalStateException("reqMessage == null");
        }
        
        dispatcher.dispatchMessage(reqMessage.setPriority(Priority.MEDIUM));
        
        try {
            final Message resMessage = dispatcher.awaitResponse(reqMessage);
            
            final CreateChannelGroupCommand command = resMessage.getCommand().getCreateChannelGroup();
            if (command.getStatus() == Status.SUCCESS) {
            
                final ChannelGroup channelGroup = new ClientChannelGroup(command.getChannelGroupName(), false, dispatcher);
                return channelGroup;
                
            } else {
                throw new MirrorCacheException(Reason.CREATE_CHANNELGROUP_FAILURE).withDetail("channelGroupName: " + command.getChannelGroupName());
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.info(Thread.currentThread().getName() + " thread interrupted.");
        }
        
        throw new IllegalStateException(new MirrorCacheException(Reason.UNKNOWN));
    }
}
