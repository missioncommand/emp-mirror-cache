package mil.emp3.mirrorcache.spi;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Transport;

public class TransportProviderFactory {
    static final private Logger LOG = LoggerFactory.getLogger(TransportProviderFactory.class);
    
    static private class Holder { // for synchronized initialization 
        static private TransportProviderFactory instance = new TransportProviderFactory();
    }
    
    final private ServiceLoader<TransportProvider> loader;
    
    private TransportProviderFactory() {
        loader = ServiceLoader.load(TransportProvider.class);
    }
    
    static private TransportProviderFactory getInstance() {
        return Holder.instance;
    }
    
    static public Transport getTransport(final TransportProvider.TransportArguments args) throws MirrorCacheException {
        LOG.debug("getTransport(): args=" + args);
        
        try {
            for (Iterator<TransportProvider> iter = getInstance().loader.iterator(); iter.hasNext(); ) {
                final TransportProvider provider = iter.next();
                
                if (provider.canHandle(args)) {
                    return provider.getTransport(args);
                }
            }
        } catch (ServiceConfigurationError e) {
            throw new MirrorCacheException(Reason.SPI_LOAD_FAILURE, e).withDetail("type: " + args.type());
        }
        
        throw new IllegalStateException("Unable to locate suitable transport implementation.");
    }
}
