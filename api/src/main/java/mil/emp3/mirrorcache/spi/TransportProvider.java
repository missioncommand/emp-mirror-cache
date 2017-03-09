package mil.emp3.mirrorcache.spi;

import mil.emp3.mirrorcache.Transport;
import mil.emp3.mirrorcache.Transport.TransportType;
import mil.emp3.mirrorcache.impl.MessageDispatcher;

public interface TransportProvider {
    
    boolean canHandle(TransportArguments args);
    Transport getTransport(TransportArguments args);
    
    interface TransportArguments {
        TransportType type();
        MessageDispatcher messageDispatcher();
    }
}
