package mil.emp3.mirrorcache.service.processor;

import org.cmapi.primitives.proto.CmapiProto.ProtoMessage;

public interface OperationProcessor {
    
    void process(String sessionId, ProtoMessage pm);
    
}
