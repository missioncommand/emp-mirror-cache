package org.cmapi.mirrorcache.service.processor;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.cmapi.mirrorcache.MirrorCacheException.Reason;
import org.cmapi.mirrorcache.Priority;
import org.cmapi.mirrorcache.service.HistoryManager;
import org.cmapi.mirrorcache.service.SessionManager;
import org.cmapi.mirrorcache.support.ProtoMessageEntry;
import org.cmapi.primitives.proto.CmapiProto.ChannelHistoryCommand;
import org.cmapi.primitives.proto.CmapiProto.HistoryInfo;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand;
import org.cmapi.primitives.proto.CmapiProto.ProtoMessage;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;

@ApplicationScoped
public class ChannelHistoryProcessor implements CommandProcessor {

    @Inject
    private Logger LOG;
    
    @Inject
    private HistoryManager historyManager;
    
    @Inject
    private SessionManager sessionManager;

    
    @Override
    public void process(String sessionId, ProtoMessage req) {
        final ChannelHistoryCommand command = req.getCommand().getChannelHistory();

        final HistoryInfo history = historyManager.getChannelHistory(sessionId,
                                                                     command.getChannelName(),
                                                                     command.getStartTime(),
                                                                     command.getEndTime());
        
        final ProtoMessage res = ProtoMessage.newBuilder(req)
                .setPriority(Priority.MEDIUM.getValue())
                .setCommand(OneOfCommand.newBuilder()
                                        .setChannelHistory(ChannelHistoryCommand.newBuilder(command)
                                                                                .setStatus(Status.SUCCESS)
                                                                                .setHistory(history)))
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
