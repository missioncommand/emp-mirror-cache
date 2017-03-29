package mil.emp3.mirrorcache.impl.transport.ws.undertow;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MessageDispatcher;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Payload;
import mil.emp3.mirrorcache.Transport;
import mil.emp3.mirrorcache.event.ClientConnectEvent;
import mil.emp3.mirrorcache.event.ClientDisconnectEvent;
import mil.emp3.mirrorcache.event.ClientMessageEvent;


@ClientEndpoint
public class AnnotatedWebSocketClientTransport implements Transport {
    static final private Logger LOG = LoggerFactory.getLogger(AnnotatedWebSocketClientTransport.class);
    
    private Session session;
    
    final private URI uri;
    final private MessageDispatcher dispatcher;
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //

    public AnnotatedWebSocketClientTransport(URI uri, MessageDispatcher dispatcher) {
        this.uri        = uri;
        this.dispatcher = dispatcher;
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    @Override
    public void connect() throws MirrorCacheException {
        LOG.debug("connect()");
        
        if (session != null) {
            disconnect();
        }
        
        final WebSocketContainer container = ContainerProvider.getWebSocketContainer(); //io.undertow.websockets.jsr.UndertowContainerProvider

        try {
            session = container.connectToServer(this, uri); // blocks
            
        } catch (IOException | DeploymentException e) {
            throw new MirrorCacheException(Reason.CONNECT_FAILURE, e);
        }
    }
    
    @Override
    public void disconnect() throws MirrorCacheException {
        LOG.debug("disconnect()");
        
        if (session != null) {
            try {
                session.close();
            } catch (IOException e) {
                throw new MirrorCacheException(Reason.DISCONNECT_FAILURE, e);
                
            } finally {
                session = null;
            }
        }
    }
    
    @Override
    public void send(Message message) {
        final byte[] payload = message.getPayload(byte[].class).getData();
        
        session.getAsyncRemote().sendBinary(ByteBuffer.wrap(payload));
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    @OnOpen
    public void onOpen(Session session) {
        LOG.debug("onOpen()");
        
        final Message message = new Message();
        message.setEventType(ClientConnectEvent.TYPE);
        
        dispatcher.dispatchEvent(new ClientConnectEvent(message));
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        LOG.debug("onClose()");
        
        final Message message = new Message();
        message.setEventType(ClientDisconnectEvent.TYPE);
        
        dispatcher.dispatchEvent(new ClientDisconnectEvent(message));
    }

    @OnMessage
    public void onMessage(byte[] messageBytes) throws MirrorCacheException {
        final Message message = new Message();
        message.setEventType(ClientMessageEvent.TYPE);
        message.setPayload(new Payload<>(null, "[B", messageBytes));
        
        //TODO place into inbound priority queue after InTransportStage
        dispatcher.getInProcessorPipeline().processMessage(message);

        dispatcher.dispatchEvent(new ClientMessageEvent(message));
    }
    
    @OnMessage
    public void onMessage(String messageStr) {
        final Message message = new Message();
        message.setEventType(ClientMessageEvent.TYPE);
        message.setPayload(new Payload<>(null, String.class.getName(), messageStr));
        
        dispatcher.dispatchEvent(new ClientMessageEvent(message));
    }
    
    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }
}
