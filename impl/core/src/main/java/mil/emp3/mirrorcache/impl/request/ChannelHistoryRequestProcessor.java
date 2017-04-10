package mil.emp3.mirrorcache.impl.request;

import java.util.ArrayList;
import java.util.List;

import org.cmapi.primitives.proto.CmapiProto.ChannelHistoryOperation;
import org.cmapi.primitives.proto.CmapiProto.HistoryInfo;
import org.cmapi.primitives.proto.CmapiProto.LogEntry;
import org.cmapi.primitives.proto.CmapiProto.OneOfOperation;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.History.Entry;
import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MessageDispatcher;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Priority;
import mil.emp3.mirrorcache.channel.ChannelHistory;
import mil.emp3.mirrorcache.impl.DefaultHistory;
import mil.emp3.mirrorcache.impl.DefaultHistory.ClientEntry;

public class ChannelHistoryRequestProcessor extends BaseRequestProcessor<Message, ChannelHistory> {
    static final private Logger LOG = LoggerFactory.getLogger(ChannelHistory.class);
    
    final private MessageDispatcher dispatcher;
    
    public ChannelHistoryRequestProcessor(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
    
    @Override
    public ChannelHistory executeSync(Message reqMessage) throws MirrorCacheException {
        if (reqMessage == null) {
            throw new IllegalStateException("reqMessage == null");
        }

        dispatcher.dispatchMessage(reqMessage.setPriority(Priority.MEDIUM));
        
        try {
            final Message resMessage = dispatcher.awaitResponse(reqMessage);
            
            final ChannelHistoryOperation operation = resMessage.getOperation().as(OneOfOperation.class).getChannelHistory();
            if (operation.getStatus() == Status.SUCCESS) {

                final List<Entry<OneOfOperation>> entries = new ArrayList<>();
                
                final HistoryInfo historyInfo = operation.getHistory();
                
                for (LogEntry logEntry : historyInfo.getLogList()) {
                    entries.add(new ClientEntry(logEntry.getId(), logEntry.getTime(), logEntry.getOperation()));
                }
                
                final ChannelHistory history = new DefaultHistory(operation.getChannelName(),
                                                                  historyInfo.getStartTime(),
                                                                  historyInfo.getEndTime(),
                                                                  entries);
                return history;
                
            } else {
                throw new MirrorCacheException(Reason.CHANNEL_HISTORY_FAILURE).withDetail("channelName: " + operation.getChannelName());
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.info(Thread.currentThread().getName() + " thread interrupted.");
        }
        
        throw new IllegalStateException(new MirrorCacheException(Reason.UNKNOWN));
    }

}
