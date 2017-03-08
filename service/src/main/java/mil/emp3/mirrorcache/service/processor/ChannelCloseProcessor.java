package mil.emp3.mirrorcache.service.processor;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.cmapi.primitives.proto.CmapiProto.ChannelCloseCommand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand;
import org.cmapi.primitives.proto.CmapiProto.ProtoMessage;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;

import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Priority;
import mil.emp3.mirrorcache.service.ChannelManager;
import mil.emp3.mirrorcache.service.SessionManager;
import mil.emp3.mirrorcache.support.ProtoMessageEntry;

@ApplicationScoped
public class ChannelCloseProcessor implements CommandProcessor {

    @Inject
    private Logger LOG;
    
    @Inject
    private ChannelManager channelManager;

    @Inject
    private SessionManager sessionManager;
    
    @Override
    public void process(String sessionId, ProtoMessage req) {
        final ChannelCloseCommand command = req.getCommand().getChannelClose();

        Status status = Status.SUCCESS;
        try {
            channelManager.channelClose(sessionId, command.getChannelName());
            
        } catch (MirrorCacheException e) {
            LOG.error("ERROR: " + e.getMessage(), e);
            status = Status.FAILURE;
        }

        final ProtoMessage res = ProtoMessage.newBuilder(req)
                .setPriority(Priority.MEDIUM.getValue())
                .setCommand(OneOfCommand.newBuilder()
                                        .setChannelClose(ChannelCloseCommand.newBuilder(command)
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