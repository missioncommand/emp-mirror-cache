package mil.emp3.mirrorcache.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cmapi.primitives.proto.CmapiProto.OneOfCommand;

import mil.emp3.mirrorcache.channel.ChannelGroupHistory;
import mil.emp3.mirrorcache.channel.ChannelHistory;

public class DefaultHistory implements ChannelHistory, ChannelGroupHistory {

    final private String channelName;
    final private long startTime;
    final private long endTime;
    final private List<Entry<OneOfCommand>> entries;
    
    public DefaultHistory(String channelName, long startTime, long endTime, List<Entry<OneOfCommand>> entries) {
        this.channelName = channelName;
        this.startTime   = startTime;
        this.endTime     = endTime;
        this.entries     = new ArrayList<>(entries);
    }
    
    @Override
    public String getName() {
        return channelName;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public long getEndTime() {
        return endTime;
    }

    @Override
    public List<Entry<OneOfCommand>> getEntries() {
        return Collections.unmodifiableList(entries);
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    static public class ClientEntry implements Entry<OneOfCommand> {
        final private int id;
        final private long time;
        final private OneOfCommand command;
        
        public ClientEntry(int id, long time, OneOfCommand command) {
            this.id      = id;
            this.time    = time;
            this.command = command;
        }
        
        @Override
        public int getId() {
            return id;
        }

        @Override
        public long getTime() {
            return time;
        }

        @Override
        public OneOfCommand getValue() {
            return command;
        }
    }
}
