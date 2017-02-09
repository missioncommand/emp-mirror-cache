package org.cmapi.mirrorcache.impl.request;

import java.util.ArrayList;
import java.util.List;

import org.cmapi.mirrorcache.History.Entry;
import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.MirrorCacheException.Reason;
import org.cmapi.mirrorcache.Priority;
import org.cmapi.mirrorcache.channel.ChannelGroupHistory;
import org.cmapi.mirrorcache.channel.ChannelHistory;
import org.cmapi.mirrorcache.impl.DefaultHistory;
import org.cmapi.mirrorcache.impl.MessageDispatcher;
import org.cmapi.mirrorcache.impl.DefaultHistory.ClientEntry;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupHistoryCommand;
import org.cmapi.primitives.proto.CmapiProto.HistoryInfo;
import org.cmapi.primitives.proto.CmapiProto.LogEntry;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand.CommandCase;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelGroupHistoryRequestProcessor extends BaseRequestProcessor<Message, ChannelGroupHistory> {
    static final private Logger LOG = LoggerFactory.getLogger(ChannelHistory.class);
    
    final private MessageDispatcher dispatcher;
    
    public ChannelGroupHistoryRequestProcessor(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
    
    @Override
    public ChannelGroupHistory executeSync(Message reqMessage) throws MirrorCacheException {
        if (reqMessage == null) {
            throw new IllegalStateException("reqMessage == null");
        }

        dispatcher.dispatchMessage(reqMessage.setPriority(Priority.MEDIUM));
        
        try {
            final Message resMessage = dispatcher.awaitResponse(reqMessage);
            
            final ChannelGroupHistoryCommand command = resMessage.getCommand(CommandCase.CHANNEL_GROUP_HISTORY);
            if (command.getStatus() == Status.SUCCESS) {

                final List<Entry<OneOfCommand>> entries = new ArrayList<>();
                
                final HistoryInfo historyInfo = command.getHistory();
                
                for (LogEntry logEntry : historyInfo.getLogList()) {
                    entries.add(new ClientEntry(logEntry.getId(), logEntry.getTime(), logEntry.getCommand()));
                }
                
                final ChannelGroupHistory history = new DefaultHistory(command.getChannelGroupName(),
                                                                       historyInfo.getStartTime(),
                                                                       historyInfo.getEndTime(),
                                                                       entries);
                return history;
                
            } else {
                throw new MirrorCacheException(Reason.CHANNELGROUP_HISTORY_FAILURE).withDetail("channelGroupName: " + command.getChannelGroupName());
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.info(Thread.currentThread().getName() + " thread interrupted.");
        }
        
        throw new IllegalStateException(new MirrorCacheException(Reason.UNKNOWN));
    }

}
