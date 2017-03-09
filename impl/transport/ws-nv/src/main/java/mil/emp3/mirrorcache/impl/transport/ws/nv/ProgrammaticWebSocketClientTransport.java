package mil.emp3.mirrorcache.impl.transport.ws.nv;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neovisionaries.ws.client.OpeningHandshakeException;
import com.neovisionaries.ws.client.StatusLine;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MessageDispatcher;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Payload;
import mil.emp3.mirrorcache.Transport;
import mil.emp3.mirrorcache.event.ClientConnectEvent;
import mil.emp3.mirrorcache.event.ClientDisconnectEvent;
import mil.emp3.mirrorcache.event.ClientMessageEvent;


public class ProgrammaticWebSocketClientTransport implements Transport {
    static final private Logger LOG = LoggerFactory.getLogger(ProgrammaticWebSocketClientTransport.class);
    
    private WebSocket socket;
    private WebSocketListener listener;
    
    final private URI uri;
    final private MessageDispatcher dispatcher;
    
    public ProgrammaticWebSocketClientTransport(URI uri, MessageDispatcher dispatcher) {
        this.uri        = uri;
        this.dispatcher = dispatcher;
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    @Override
    public void connect() throws MirrorCacheException {
        LOG.info("connect()");
        
        disconnect();

        try {
            socket = new WebSocketFactory().createSocket(uri, 5000);
            socket.addListener(listener = new LocalWebSocketAdapter());

            socket.connect(); // blocking call

        } catch (OpeningHandshakeException e) { // WebSocket violation !
            dumpNvLog(e);
            throw new MirrorCacheException(Reason.CONNECT_FAILURE, e);

        } catch (Exception e) {
            throw new MirrorCacheException(Reason.CONNECT_FAILURE, e);
        }
    }

    @Override
    public void disconnect() throws MirrorCacheException {
        if (socket != null) {
            try {
                socket.disconnect();
                
                if (listener != null) {
                    socket.removeListener(listener);
                    listener = null;
                }
                
            } finally {
                socket = null;
            }
        }
    }

    @Override
    public void send(Message message) throws MirrorCacheException {
        final byte[] payload = message.getPayload(byte[].class).getData();
        
        socket.sendBinary(payload);
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    static private void dumpNvLog(OpeningHandshakeException e) {
        final StatusLine sl = e.getStatusLine();
        
        LOG.error("=== Status Line ===");
        LOG.error("HTTP Version  = %s\n", sl.getHttpVersion());
        LOG.error("Status Code   = %d\n", sl.getStatusCode());
        LOG.error("Reason Phrase = %s\n", sl.getReasonPhrase());

        LOG.error("=== HTTP Headers ===");
        for (Entry<String, List<String>> entry : e.getHeaders().entrySet()) {
            final String name = entry.getKey();
            final List<String> values = entry.getValue();

            if (values == null || values.size() == 0) {
                LOG.error(name);
                continue;
            }

            for (String value : values) {
                LOG.error("%s: %s\n", name, value);
            }
        }
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    private class LocalWebSocketAdapter extends WebSocketAdapter {
        @Override
        public void onBinaryMessage(WebSocket websocket, byte[] messageBytes) throws Exception {
            final Message message = new Message();
            message.setEventType(ClientMessageEvent.TYPE);
            message.setPayload(new Payload<>(null, "[B", messageBytes));
            
            //TODO place into inbound priority queue after InTransportStage
            dispatcher.getInProcessorPipeline().processMessage(message);

            dispatcher.dispatchEvent(new ClientMessageEvent(message));
        }
        
        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            final Message message = new Message();
            message.setEventType(ClientConnectEvent.TYPE);
            
            dispatcher.dispatchEvent(new ClientConnectEvent(message));
        }
        
        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            final Message message = new Message();
            message.setEventType(ClientDisconnectEvent.TYPE);
            
            dispatcher.dispatchEvent(new ClientDisconnectEvent(message));
            
            socket = null;
        }
        
        @Override
        public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
            cause.printStackTrace(); // can be called multiple times for multiple errors
        }
        
        @Override
        public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
            LOG.error(cause.getMessage(), cause); // called when the above callbacks fail
        }
    }
}
