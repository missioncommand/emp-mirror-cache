package mil.emp3.mirrorcache.service.processor;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.cmapi.primitives.proto.CmapiProto.CreateChannelOperation;
import org.cmapi.primitives.proto.CmapiProto.OneOfOperation;
import org.cmapi.primitives.proto.CmapiProto.ProtoMessage;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;

import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.channel.Channel;
import mil.emp3.mirrorcache.service.ChannelManager;
import mil.emp3.mirrorcache.service.SessionManager;
import mil.emp3.mirrorcache.service.support.ProtoMessageEntry;

@ApplicationScoped
public class CreateChannelProcessor implements OperationProcessor {

    @Inject
    private Logger LOG;
    
    @Inject
    private ChannelManager channelManager;
    
    @Inject
    private SessionManager sessionManager;
    
    @Override
    public void process(String sessionId, ProtoMessage req) {
        final CreateChannelOperation operation = req.getOperation().getCreateChannel();
        
        Status status = Status.SUCCESS;
        try {
            channelManager.createChannel(sessionId,
                                         operation.getChannelName(),
                                         Channel.Visibility.valueOf(operation.getVisibility()),
                                         Channel.Type.valueOf(operation.getType()));
            
        } catch (MirrorCacheException e) {
            LOG.error("ERROR: " + e.getMessage(), e);
            status = Status.FAILURE;
        }

        final ProtoMessage res = ProtoMessage.newBuilder(req)
                                             .setOperation(OneOfOperation.newBuilder()
                                                                         .setCreateChannel(CreateChannelOperation.newBuilder(operation)
                                                                         .setStatus(status)))
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
