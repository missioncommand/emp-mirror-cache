package mil.emp3.mirrorcache.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.cmapi.primitives.proto.CmapiProto.HistoryInfo;
import org.cmapi.primitives.proto.CmapiProto.LogEntry;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand;

import mil.emp3.mirrorcache.History.Entry;
import mil.emp3.mirrorcache.channel.ChannelGroupHistory;
import mil.emp3.mirrorcache.channel.ChannelHistory;

@ApplicationScoped
public class HistoryManager {

    private Map<String, ChannelHistory<OneOfCommand>> channelHistoryMap;           // map<channelName, history>
    private Map<String, ChannelGroupHistory<OneOfCommand>> channelGroupHistoryMap; // map<channelGroupName, history>
    
    @PostConstruct
    public void init() {
        channelHistoryMap      = new HashMap<>();
        channelGroupHistoryMap = new HashMap<>();
    }
    
//    -- TODO remove history when channel/group is closed.
    
    public HistoryInfo getChannelHistory(String sessionId, String channelName, long startTime, long endTime) {
        if (channelHistoryMap.containsKey(channelName)) {
            
            final List<LogEntry> entries = new ArrayList<>();
            
            final ChannelHistory<OneOfCommand> channelHistory = channelHistoryMap.get(channelName);
            for (Entry<OneOfCommand> entry : channelHistory.getEntries()) {
                entries.add(LogEntry.newBuilder()
                                    .setId(entry.getId())
                                    .setTime(entry.getTime())
                                    .setCommand(entry.getValue())
                                    .build());
            }
            
            return HistoryInfo.newBuilder()
                              .setStartTime(startTime)
                              .setEndTime(endTime)
                              .addAllLog(entries)
                              .build();
            
        } else {
            return HistoryInfo.newBuilder().build();
        }
    }
    
    public HistoryInfo getChannelGroupHistory(String sessionId, String channelGroupName, long startTime, long endTime) {
        if (channelGroupHistoryMap.containsKey(channelGroupName)) {
            
            final List<LogEntry> entries = new ArrayList<>();
            
            final ChannelGroupHistory<OneOfCommand> channelGroupHistory = channelGroupHistoryMap.get(channelGroupName);
            for (Entry<OneOfCommand> entry : channelGroupHistory.getEntries()) {
                entries.add(LogEntry.newBuilder()
                                    .setId(entry.getId())
                                    .setTime(entry.getTime())
                                    .setCommand(entry.getValue())
                                    .build());
            }
            
            return HistoryInfo.newBuilder()
                              .setStartTime(startTime)
                              .setEndTime(endTime)
                              .addAllLog(entries)
                              .build();
            
        } else {
            return HistoryInfo.newBuilder().build();
        }
    }
    
    public void logChannelEntry(String sessionId, String channelName, OneOfCommand command) {
        throw new RuntimeException("not implemented");
    }
    public void logChannelGroupEntry(String sessionId, String channelGroupName, OneOfCommand command) {
        throw new RuntimeException("not implemented");
    }
}
