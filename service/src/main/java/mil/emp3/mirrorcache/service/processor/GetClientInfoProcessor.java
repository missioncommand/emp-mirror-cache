package mil.emp3.mirrorcache.service.processor;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.cmapi.primitives.proto.CmapiProto.GetClientInfoCommmand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand;
import org.cmapi.primitives.proto.CmapiProto.ProtoClientInfo;
import org.cmapi.primitives.proto.CmapiProto.ProtoMessage;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;

import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.service.SessionManager;
import mil.emp3.mirrorcache.support.ProtoMessageEntry;

@ApplicationScoped
public class GetClientInfoProcessor implements CommandProcessor {

    @Inject
    private Logger LOG;
    
    @Inject
    private SessionManager sessionManager;
    
    @Override
    public void process(String sessionId, ProtoMessage req) {
        final GetClientInfoCommmand command = req.getCommand().getGetClientInfo();
        
        final ProtoClientInfo clientInfo = ProtoClientInfo.newBuilder()
                                                          .setClientId(sessionId)
                                                          .build();
        
        final ProtoMessage res = ProtoMessage.newBuilder(req)
                                             .setCommand(OneOfCommand.newBuilder().setGetClientInfo(GetClientInfoCommmand.newBuilder(command)
                                                                                                                         .setClientInfo(clientInfo)
                                                                                                                         .setStatus(Status.SUCCESS)))
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
