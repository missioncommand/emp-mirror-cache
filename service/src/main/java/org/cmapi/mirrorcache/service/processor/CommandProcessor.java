package org.cmapi.mirrorcache.service.processor;

import org.cmapi.primitives.proto.CmapiProto.ProtoMessage;

public interface CommandProcessor {
    
    void process(String sessionId, ProtoMessage pm);
    
}
