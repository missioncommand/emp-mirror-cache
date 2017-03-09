package mil.emp3.mirrorcache.spi;

import java.net.URI;

import mil.emp3.mirrorcache.MirrorCacheClient;
import mil.emp3.mirrorcache.Transport.TransportType;

public interface MirrorCacheClientProvider {

    boolean canHandle(ClientArguments args);
    MirrorCacheClient getClient(ClientArguments args);
    
    interface ClientArguments {
        URI endpoint();
        TransportType transportType();
    }
}
