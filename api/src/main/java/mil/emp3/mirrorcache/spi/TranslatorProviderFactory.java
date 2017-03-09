package mil.emp3.mirrorcache.spi;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Translator;

public class TranslatorProviderFactory {
    static private class Holder { // for synchronized initialization 
        static private TranslatorProviderFactory instance = new TranslatorProviderFactory();
    }
    
    final private ServiceLoader<TranslatorProvider> loader;
    
    private TranslatorProviderFactory() {
        loader = ServiceLoader.load(TranslatorProvider.class);
    }
    
    static private TranslatorProviderFactory getInstance() {
        return Holder.instance;
    }

    static public Translator getTranslator(String from) throws MirrorCacheException {
        try {
            for (Iterator<TranslatorProvider> iter = getInstance().loader.iterator(); iter.hasNext(); ) {
                final TranslatorProvider provider = iter.next();
                
                if (provider.canTranslateFrom(from)) {
                    return provider.getTranslator();
                }
            }
            
        } catch (ServiceConfigurationError e) {
            throw new MirrorCacheException(Reason.SPI_LOAD_FAILURE, e).withDetail("from: " + from);
        }
        
        throw new IllegalStateException("Unable to locate suitable translator.");
    }
}
