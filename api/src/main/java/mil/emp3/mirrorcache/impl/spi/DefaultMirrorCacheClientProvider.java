package mil.emp3.mirrorcache.impl.spi;

import java.net.URI;

import mil.emp3.mirrorcache.MirrorCacheClient;
import mil.emp3.mirrorcache.impl.DefaultMirrorCacheClient;
import mil.emp3.mirrorcache.spi.MirrorCacheClientProvider;

public class DefaultMirrorCacheClientProvider implements MirrorCacheClientProvider {

    private MirrorCacheClient client;
    
    @Override
    public boolean canHandle(URI endpoint) {
        return true;
    }

    @Override
    public MirrorCacheClient getClient(URI endpoint) {
        if (client == null) {
            client = new DefaultMirrorCacheClient(endpoint);
        }
        return client;
    }
    
}
