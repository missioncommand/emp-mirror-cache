package org.cmapi.mirrorcache.spi;

import java.net.URI;

import org.cmapi.mirrorcache.MirrorCacheClient;

public interface MirrorCacheClientProvider {

    boolean canHandle(URI endpoint);
    
    MirrorCacheClient getClient(URI endpoint);
}
