package mil.emp3.mirrorcache.service;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.cmapi.primitives.proto.CmapiProto.ProtoMessage;
import org.slf4j.Logger;

import mil.emp3.mirrorcache.service.event.ConnectEvent;
import mil.emp3.mirrorcache.service.event.DisconnectEvent;
import mil.emp3.mirrorcache.service.event.MessageEvent;
import mil.emp3.mirrorcache.support.ItemTracker;
import mil.emp3.mirrorcache.support.Utils;

@ServerEndpoint(
    value = "/mirrorcache",
    encoders = WebSocketEncoder.class,
    decoders = WebSocketDecoder.class
)
public class WebSocketServerEndpoint {

    @Inject
    private Logger LOG;
    
    @Inject
    private Event<MessageEvent> onMessageEvent;
    
    @Inject
    private Event<DisconnectEvent> onDisconnectEvent;
    
    @Inject
    private Event<ConnectEvent> onConnectEvent;
    
    private ItemTracker tracker;
    
    @PostConstruct
    public void init() {
        tracker = new ItemTracker(LOG);
    }
    
    @OnOpen
    public void onOpen(Session session) {
        LOG.debug("onConnect(" + session.getId() + ")");
        tracker.setId(session.getId());
        onConnectEvent.fire(new ConnectEvent(session.getId(), session));
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        LOG.debug("onDisconnect(" + session.getId() + ")");
        onDisconnectEvent.fire(new DisconnectEvent(session.getId(), session, reason));
    }
    
    @OnMessage
    public void onMessage(ProtoMessage req, Session session) throws Exception {
        LOG.debug("onMessage(" + req.getSerializedSize() + "): " + Utils.asString(req));
        
        tracker.track();
          if (tracker.getReceiveCount() % 1000 == 0) { // update every 1000
              LOG.debug("receiveCount(" + tracker.getId() + "): " + tracker.getReceiveCount());
          }

        onMessageEvent.fire(new MessageEvent(session.getId(), req));
    }
    
    @OnMessage
    public void onMessage(String message, Session session) {
        LOG.debug("___onMessage(String): " + message);
        
        for (Session s : session.getOpenSessions()) {
            s.getAsyncRemote().sendText(message);
        }
    }
}