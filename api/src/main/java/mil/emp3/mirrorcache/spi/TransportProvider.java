package mil.emp3.mirrorcache.spi;

import java.net.URI;

import mil.emp3.mirrorcache.MessageDispatcher;
import mil.emp3.mirrorcache.Transport;
import mil.emp3.mirrorcache.Transport.TransportType;

public interface TransportProvider {
    
    boolean canHandle(TransportArguments args);
    Transport getTransport(TransportArguments args);
    
    interface TransportArguments {
        TransportType type();
        URI endpoint();
        MessageDispatcher messageDispatcher();
    }
}
