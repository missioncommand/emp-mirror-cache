package mil.emp3.mirrorcache.impl.spi;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Serializer;
import mil.emp3.mirrorcache.spi.SerializerProvider;

public class SerializerProviderFactory {
    static final private Logger LOG = LoggerFactory.getLogger(SerializerProviderFactory.class);

    static private class Holder { // for synchronized initialization 
        static private SerializerProviderFactory instance = new SerializerProviderFactory();
    }
    
    final private ServiceLoader<SerializerProvider> loader;
    
    private SerializerProviderFactory() {
        loader = ServiceLoader.load(SerializerProvider.class);
    }
    
    static private SerializerProviderFactory getInstance() {
        return Holder.instance;
    }
    
    static public Serializer getSerializer(String type) throws MirrorCacheException {
        try {
            for (Iterator<SerializerProvider> iter = getInstance().loader.iterator(); iter.hasNext(); ) {
                final SerializerProvider provider = iter.next();
                
                if (provider.canHandle(type)) {
                    return provider.getSerializer();
                }
            }
            
        } catch (ServiceConfigurationError e) {
            throw new MirrorCacheException(Reason.SPI_LOAD_FAILURE, e)
                                          .withDetail("type: " + type);
        }
        
        LOG.warn("Unable to locate suitable serializer.");
        return null;
    }
}