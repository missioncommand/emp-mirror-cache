package org.cmapi.mirrorcache.service.event;

import javax.websocket.Session;

public class ConnectEvent {

    final private String sessionId;
    final private Session session;
    
    public ConnectEvent(String sessionId, Session session) {
        this.sessionId = sessionId;
        this.session = session;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public Session getSession() {
        return session;
    }
}
