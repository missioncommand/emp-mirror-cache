package mil.emp3.mirrorcache.service.event;

import javax.websocket.CloseReason;
import javax.websocket.Session;

public class DisconnectEvent {

    final private String sessionId;
    final private Session session;
    final private CloseReason reason;
    
    public DisconnectEvent(String sessionId, Session session, CloseReason reason) {
        this.sessionId = sessionId;
        this.session   = session;
        this.reason    = reason;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    public Session getSession() {
        return session;
    }
    public CloseReason getReason() {
        return reason;
    }
}
