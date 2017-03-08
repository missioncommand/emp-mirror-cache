package mil.emp3.mirrorcache.impl.request;

import org.cmapi.primitives.proto.CmapiProto.CreateChannelCommand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand.CommandCase;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Priority;
import mil.emp3.mirrorcache.channel.Channel;
import mil.emp3.mirrorcache.impl.MessageDispatcher;
import mil.emp3.mirrorcache.impl.channel.ClientChannel;

public class CreateChannelRequestProcessor extends BaseRequestProcessor<Message, Channel> {
    static final private Logger LOG = LoggerFactory.getLogger(CreateChannelRequestProcessor.class);
    
    final private MessageDispatcher dispatcher;
    
    public CreateChannelRequestProcessor(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public Channel executeSync(Message reqMessage) throws MirrorCacheException {
        if (reqMessage == null) {
            throw new IllegalStateException("reqMessage == null");
        }

        dispatcher.dispatchMessage(reqMessage.setPriority(Priority.MEDIUM));
        
        try {
            final Message resMessage = dispatcher.awaitResponse(reqMessage);
            
            final CreateChannelCommand command = resMessage.getCommand(CommandCase.CREATE_CHANNEL);
            if (command.getStatus() == Status.SUCCESS) {
            
                final Channel channel = new ClientChannel(command.getChannelName(),
                                                          Channel.Visibility.valueOf(command.getVisibility()),
                                                          Channel.Type.valueOf(command.getType()),
                                                          false,
                                                          dispatcher);
                return channel;
                
            } else {
                throw new MirrorCacheException(Reason.CREATE_CHANNEL_FAILURE).withDetail("channelName: " + command.getChannelName());
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.info(Thread.currentThread().getName() + " thread interrupted.");
        }
        
        throw new IllegalStateException(new MirrorCacheException(Reason.UNKNOWN));
    }
}
