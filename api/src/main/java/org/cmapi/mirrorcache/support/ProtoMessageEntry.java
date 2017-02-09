package org.cmapi.mirrorcache.support;

import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.cmapi.primitives.proto.CmapiProto.ProtoMessage;

/** 
 * Intended to be used with a {@link PriorityQueue}. Supports ordering based
 * first on message priority, and then on insertion order.
 */
public class ProtoMessageEntry implements Comparable<ProtoMessageEntry> {
    static final private AtomicLong seq = new AtomicLong(0);
    
    final private ProtoMessage entry;
    final private long seqNum;
    
    public ProtoMessageEntry(ProtoMessage entry) {
        this.entry  = entry;
        this.seqNum = seq.getAndIncrement();
    }
    
    public ProtoMessage getEntry() {
        return entry;
    }
    public long getSeqNum() {
        return seqNum;
    }

    @Override
    public int compareTo(ProtoMessageEntry other) {
        int result = entry.getPriority() - other.getEntry().getPriority();
        if (result == 0) {
            result = seqNum < other.seqNum ? -1 : 1;
        }
        
        return result;
    }
}
