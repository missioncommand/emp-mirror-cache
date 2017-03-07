package mil.emp3.mirrorcache.spi;

import java.net.URI;

import mil.emp3.mirrorcache.MirrorCacheClient;

public interface MirrorCacheClientProvider {

    boolean canHandle(URI endpoint);
    
    MirrorCacheClient getClient(URI endpoint);
}
