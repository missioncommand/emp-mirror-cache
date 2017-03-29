package mil.emp3.mirrorcache.service.processor;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.cmapi.primitives.proto.CmapiProto.ChannelGroupOpenCommand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand;
import org.cmapi.primitives.proto.CmapiProto.ProtoMessage;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;

import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Priority;
import mil.emp3.mirrorcache.service.ChannelGroupManager;
import mil.emp3.mirrorcache.service.SessionManager;
import mil.emp3.mirrorcache.support.ProtoMessageEntry;

@ApplicationScoped
public class ChannelGroupOpenProcessor implements CommandProcessor {

    @Inject
    private Logger LOG;
    
    @Inject
    private ChannelGroupManager channelGroupManager;
    
    @Inject
    private SessionManager sessionManager;

    @Override
    public void process(String sessionId, ProtoMessage req) {
        final ChannelGroupOpenCommand command = req.getCommand().getChannelGroupOpen();

        Status status = Status.SUCCESS;
        try {
            channelGroupManager.channelGroupOpen(sessionId, command.getChannelGroupName());
            
        } catch (MirrorCacheException e) {
            LOG.error("ERROR", e);
            status = Status.FAILURE;
        }

        final ProtoMessage res = ProtoMessage.newBuilder(req)
                .setPriority(Priority.MEDIUM.getValue())
                .setCommand(OneOfCommand.newBuilder()
                                        .setChannelGroupOpen(ChannelGroupOpenCommand.newBuilder(command)
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
