package org.cmapi.mirrorcache.impl.spi;

import java.net.URI;

import org.cmapi.mirrorcache.MirrorCacheClient;
import org.cmapi.mirrorcache.impl.DefaultMirrorCacheClient;
import org.cmapi.mirrorcache.spi.MirrorCacheClientProvider;

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
