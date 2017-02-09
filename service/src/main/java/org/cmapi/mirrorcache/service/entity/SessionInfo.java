package org.cmapi.mirrorcache.service.entity;

public class SessionInfo {

    private String sessionId;
    private String agent;
    private int outboundQueueSize;

    public String getSessionId() {
        return sessionId;
    }
    public String getAgent() {
        return agent;
    }
    public int getOutboundQueueSize() {
        return outboundQueueSize;
    }

    @Override
    public String toString() {
        return "SessionInfo [sessionId=" + sessionId + ", agent=" + agent + ", outboundQueueSize=" + outboundQueueSize + "]";
    }

    static public class Builder {
        private SessionInfo instance;
        
        public Builder() {
            instance = new SessionInfo();
        }
        
        public Builder setSessionId(String sessionId) {
            instance.sessionId = sessionId;
            return this;
        }
        public Builder setAgent(String agent) {
            instance.agent = agent;
            return this;
        }
        public Builder setOutboundQueueSize(int outboundQueueSize) {
            instance.outboundQueueSize = outboundQueueSize;
            return this;
        }
        
        public SessionInfo build() {
            return instance;
        }
    }
}
