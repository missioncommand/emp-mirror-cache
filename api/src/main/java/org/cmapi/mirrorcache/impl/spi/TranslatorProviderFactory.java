package org.cmapi.mirrorcache.impl.spi;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.MirrorCacheException.Reason;
import org.cmapi.mirrorcache.Translator;
import org.cmapi.mirrorcache.spi.TranslatorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TranslatorProviderFactory {
    static final private Logger LOG = LoggerFactory.getLogger(TranslatorProviderFactory.class);

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
            throw new MirrorCacheException(Reason.SPI_LOAD_FAILURE, e);
        }
        
        LOG.warn("Unable to locate suitable translator.");
        return null;
    }

}
