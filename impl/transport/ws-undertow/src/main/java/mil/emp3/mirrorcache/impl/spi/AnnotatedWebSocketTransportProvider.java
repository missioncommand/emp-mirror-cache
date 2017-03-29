package mil.emp3.mirrorcache.impl.spi;

import mil.emp3.mirrorcache.Transport;
import mil.emp3.mirrorcache.Transport.TransportType;
import mil.emp3.mirrorcache.impl.transport.ws.undertow.AnnotatedWebSocketClientTransport;
import mil.emp3.mirrorcache.spi.TransportProvider;

public class AnnotatedWebSocketTransportProvider implements TransportProvider {

    @Override
    public boolean canHandle(TransportArguments args) {
        return    args.type() == TransportType.WEBSOCKET
               || args.type() == TransportType.WEBSOCKET_ANNOTATED;
    }
    
    @Override
    public Transport getTransport(TransportArguments args) {
        final Transport transport = new AnnotatedWebSocketClientTransport(args.endpoint(), args.messageDispatcher());
        return transport;
    }

}
