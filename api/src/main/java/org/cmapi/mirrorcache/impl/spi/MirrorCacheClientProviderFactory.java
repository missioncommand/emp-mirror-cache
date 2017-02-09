package org.cmapi.mirrorcache.impl.spi;

import java.net.URI;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.cmapi.mirrorcache.MirrorCacheClient;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.MirrorCacheException.Reason;
import org.cmapi.mirrorcache.spi.MirrorCacheClientProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MirrorCacheClientProviderFactory {
    static final private Logger LOG = LoggerFactory.getLogger(MirrorCacheClientProviderFactory.class);

    static private class Holder { // for synchronized initialization 
        static private MirrorCacheClientProviderFactory instance = new MirrorCacheClientProviderFactory();
    }
    
    final private ServiceLoader<MirrorCacheClientProvider> loader;
    
    private MirrorCacheClientProviderFactory() {
        loader = ServiceLoader.load(MirrorCacheClientProvider.class);
    }
    
    static private MirrorCacheClientProviderFactory getInstance() {
        return Holder.instance;
    }
    
    static public MirrorCacheClient getClient(URI endpoint) throws MirrorCacheException {
        try {
            for (Iterator<MirrorCacheClientProvider> iter = getInstance().loader.iterator(); iter.hasNext(); ) {
                final MirrorCacheClientProvider provider = iter.next();
                
                if (provider.canHandle(endpoint)) {
                    return provider.getClient(endpoint);
                }
            }
            
        } catch (ServiceConfigurationError e) {
            throw new MirrorCacheException(Reason.SPI_LOAD_FAILURE, e)
                                          .withDetail("endpoint: " + endpoint);
        }
        
        LOG.warn("Unable to locate suitable client.");
        return null;
    }

}
