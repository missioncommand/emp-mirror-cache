package org.cmapi.mirrorcache.service.processor;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.cmapi.mirrorcache.MirrorCacheException.Reason;
import org.cmapi.mirrorcache.Priority;
import org.cmapi.mirrorcache.service.ChannelGroupManager;
import org.cmapi.mirrorcache.service.SessionManager;
import org.cmapi.mirrorcache.support.ProtoMessageEntry;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupInfo;
import org.cmapi.primitives.proto.CmapiProto.FindChannelGroupsCommand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand;
import org.cmapi.primitives.proto.CmapiProto.ProtoMessage;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;

@ApplicationScoped
public class FindChannelGroupsProcessor implements CommandProcessor {

    @Inject
    Logger LOG;
    
    @Inject
    private ChannelGroupManager channelGroupManager;
    
    @Inject
    private SessionManager sessionManager;

    
    @Override
    public void process(String sessionId, ProtoMessage req) {
        final FindChannelGroupsCommand command = req.getCommand().getFindChannelGroups();
        
        final List<ChannelGroupInfo> channelGroupInfos = channelGroupManager.findChannelGroups(sessionId, command.getFilter());
        
        final ProtoMessage res = ProtoMessage.newBuilder(req)
                .setPriority(Priority.MEDIUM.getValue())
                .setCommand(OneOfCommand.newBuilder()
                                        .setFindChannelGroups(FindChannelGroupsCommand.newBuilder(command)
                                                                                      .addAllChannelGroup(channelGroupInfos)
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
