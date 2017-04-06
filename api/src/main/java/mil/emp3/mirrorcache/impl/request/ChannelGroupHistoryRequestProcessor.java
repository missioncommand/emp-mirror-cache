package mil.emp3.mirrorcache.impl.request;

import java.util.ArrayList;
import java.util.List;

import org.cmapi.primitives.proto.CmapiProto.ChannelGroupHistoryCommand;
import org.cmapi.primitives.proto.CmapiProto.HistoryInfo;
import org.cmapi.primitives.proto.CmapiProto.LogEntry;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.History.Entry;
import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MessageDispatcher;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Priority;
import mil.emp3.mirrorcache.channel.ChannelGroupHistory;
import mil.emp3.mirrorcache.channel.ChannelHistory;
import mil.emp3.mirrorcache.impl.DefaultHistory;
import mil.emp3.mirrorcache.impl.DefaultHistory.ClientEntry;

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
            
            final ChannelGroupHistoryCommand command = resMessage.getCommand().getChannelGroupHistory();
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
