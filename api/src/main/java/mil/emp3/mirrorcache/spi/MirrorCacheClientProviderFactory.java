package mil.emp3.mirrorcache.spi;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import mil.emp3.mirrorcache.MirrorCacheClient;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;

public class MirrorCacheClientProviderFactory {
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
    
    static public MirrorCacheClient getClient(final MirrorCacheClientProvider.ClientArguments args) throws MirrorCacheException {
        try {
            for (Iterator<MirrorCacheClientProvider> iter = getInstance().loader.iterator(); iter.hasNext(); ) {
                final MirrorCacheClientProvider provider = iter.next();
                
                if (provider.canHandle(args)) {
                    return provider.getClient(args);
                }
            }
        } catch (ServiceConfigurationError e) {
            throw new MirrorCacheException(Reason.SPI_LOAD_FAILURE, e).withDetail("endpoint: " + args.endpoint());
        }
        
        throw new IllegalStateException("Unable to locate suitable client.");
    }
}
