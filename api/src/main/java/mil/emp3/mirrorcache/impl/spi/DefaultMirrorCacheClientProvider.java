package mil.emp3.mirrorcache.impl.spi;

import mil.emp3.mirrorcache.MirrorCacheClient;
import mil.emp3.mirrorcache.Transport.TransportType;
import mil.emp3.mirrorcache.impl.DefaultMirrorCacheClient;
import mil.emp3.mirrorcache.spi.MirrorCacheClientProvider;

public class DefaultMirrorCacheClientProvider implements MirrorCacheClientProvider {

    private MirrorCacheClient client;
    
    @Override
    public boolean canHandle(ClientArguments args) {
        return    args.transportType() == TransportType.WEBSOCKET
               || args.transportType() == TransportType.WEBSOCKET_ANNOTATED
               || args.transportType() == TransportType.WEBSOCKET_PROGRAMMATIC;
    }
    
    @Override
    public MirrorCacheClient getClient(ClientArguments args) {
        if (client == null) {
            client = new DefaultMirrorCacheClient(args);
        }
        return client;
    }
}
