package mil.emp3.mirrorcache.spi;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Serializer;

public class SerializerProviderFactory {
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
            throw new MirrorCacheException(Reason.SPI_LOAD_FAILURE, e).withDetail("type: " + type);
        }
        
        throw new IllegalStateException("Unable to locate suitable serializer.");
    }
}