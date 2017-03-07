package mil.emp3.mirrorcache.impl.spi;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.Deserializer;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.spi.DeserializerProvider;

public class DeserializerProviderFactory {
    static final private Logger LOG = LoggerFactory.getLogger(DeserializerProviderFactory.class);

    static private class Holder { // for synchronized initialization 
        static private DeserializerProviderFactory instance = new DeserializerProviderFactory();
    }
    
    final private ServiceLoader<DeserializerProvider> loader;
    
    private DeserializerProviderFactory() {
        loader = ServiceLoader.load(DeserializerProvider.class);
    }
    
    static private DeserializerProviderFactory getInstance() {
        return Holder.instance;
    }
    
    static public Deserializer getDeserializer(String type) throws MirrorCacheException {
        try {
            for (Iterator<DeserializerProvider> iter = getInstance().loader.iterator(); iter.hasNext(); ) {
                final DeserializerProvider provider = iter.next();
                
                if (provider.canHandle(type)) {
                    return provider.getDeserializer();
                }
            }
            
        } catch (ServiceConfigurationError e) {
            throw new MirrorCacheException(Reason.SPI_LOAD_FAILURE, e)
                                          .withDetail("type: " + type);
        }
        
        LOG.warn("Unable to locate suitable deserializer.");
        return null;
    }
}
