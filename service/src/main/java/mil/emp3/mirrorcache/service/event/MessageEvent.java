package mil.emp3.mirrorcache.service.event;

import org.cmapi.primitives.proto.CmapiProto.ProtoMessage;

public class MessageEvent {

    final private String sessionId;
    final private ProtoMessage message;
    
    public MessageEvent(String sessionId, ProtoMessage message) {
        this.sessionId = sessionId;
        this.message   = message;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    public ProtoMessage getMessage() {
        return message;
    }
}
