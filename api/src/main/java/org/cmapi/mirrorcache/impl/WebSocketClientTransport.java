package org.cmapi.mirrorcache.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.MirrorCacheException.Reason;
import org.cmapi.mirrorcache.Payload;
import org.cmapi.mirrorcache.Transport;
import org.cmapi.mirrorcache.event.ClientConnectEvent;
import org.cmapi.mirrorcache.event.ClientDisconnectEvent;
import org.cmapi.mirrorcache.event.ClientMessageEvent;


@ClientEndpoint
public class WebSocketClientTransport implements Transport {
    private Session session;
    
    final private URI uri;
    final private MessageDispatcher dispatcher;
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //

    public WebSocketClientTransport(URI uri, MessageDispatcher dispatcher) {
        this.uri        = uri;
        this.dispatcher = dispatcher;
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    @Override
    public void connect() throws MirrorCacheException {
        final WebSocketContainer container = ContainerProvider.getWebSocketContainer(); //io.undertow.websockets.jsr.UndertowContainerProvider

        try {
            session = container.connectToServer(this, uri);
            
        } catch (IOException | DeploymentException e) {
            throw new MirrorCacheException(Reason.CONNECT_FAILURE, e);
        }
    }
    
    @Override
    public void disconnect() throws MirrorCacheException {
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
        final Message message = new Message();
        message.setEventType(ClientConnectEvent.TYPE);
        
        dispatcher.dispatchEvent(new ClientConnectEvent(message));
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        final Message message = new Message();
        message.setEventType(ClientDisconnectEvent.TYPE);
        
        dispatcher.dispatchEvent(new ClientDisconnectEvent(message));
        
        this.session = null;
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
}
