package mil.emp3.mirrorcache.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cmapi.primitives.proto.CmapiProto.OneOfOperation;

import mil.emp3.mirrorcache.channel.ChannelGroupHistory;
import mil.emp3.mirrorcache.channel.ChannelHistory;

public class DefaultHistory implements ChannelHistory<OneOfOperation>, ChannelGroupHistory<OneOfOperation> {

    final private String channelName;
    final private long startTime;
    final private long endTime;
    final private List<Entry<OneOfOperation>> entries;
    
    public DefaultHistory(String channelName, long startTime, long endTime, List<Entry<OneOfOperation>> entries) {
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
    public List<Entry<OneOfOperation>> getEntries() {
        return Collections.unmodifiableList(entries);
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    static public class ClientEntry implements Entry<OneOfOperation> {
        final private int id;
        final private long time;
        final private OneOfOperation operation;
        
        public ClientEntry(int id, long time, OneOfOperation operation) {
            this.id        = id;
            this.time      = time;
            this.operation = operation;
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
        public OneOfOperation getValue() {
            return operation;
        }
    }
}
