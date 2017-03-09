package mil.emp3.mirrorcache.impl.spi;

import mil.emp3.mirrorcache.Transport;
import mil.emp3.mirrorcache.Transport.TransportType;
import mil.emp3.mirrorcache.impl.transport.ws.nv.ProgrammaticWebSocketClientTransport;
import mil.emp3.mirrorcache.spi.TransportProvider;

public class ProgrammaticWebSocketTransportProvider implements TransportProvider {

    @Override
    public boolean canHandle(TransportArguments args) {
        return    args.type() == TransportType.WEBSOCKET
               || args.type() == TransportType.WEBSOCKET_PROGRAMMATIC;
    }
    
    @Override
    public Transport getTransport(TransportArguments args) {
        final Transport transport = new ProgrammaticWebSocketClientTransport(args.endpoint(), args.messageDispatcher());
        return transport;
    }
}
