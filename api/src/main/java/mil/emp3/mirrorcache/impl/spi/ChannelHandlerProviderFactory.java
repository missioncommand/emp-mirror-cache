package mil.emp3.mirrorcache.impl.spi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.channel.ChannelHandler;
import mil.emp3.mirrorcache.spi.ChannelHandlerProvider;

public class ChannelHandlerProviderFactory {
    static final private Logger LOG = LoggerFactory.getLogger(ChannelHandlerProviderFactory.class);

    static private class Holder { // for synchronized initialization 
        static private ChannelHandlerProviderFactory instance = new ChannelHandlerProviderFactory();
    }
    
    final private ServiceLoader<ChannelHandlerProvider> loader;
    
    private ChannelHandlerProviderFactory() {
        loader = ServiceLoader.load(ChannelHandlerProvider.class);
    }
    
    static private ChannelHandlerProviderFactory getInstance() {
        return Holder.instance;
    }
    
    
    static public List<ChannelHandler> getChannelHandlers(String channelName) throws MirrorCacheException {
        final List<ChannelHandler> results = new ArrayList<>();
        
        try {
            for (Iterator<ChannelHandlerProvider> iter = getInstance().loader.iterator(); iter.hasNext(); ) {
                final ChannelHandlerProvider provider = iter.next();
                
                if (provider.canHandle(channelName)) {
                    results.add(provider.getChannelHandler());
                }
            }
            
        } catch (ServiceConfigurationError e) {
            throw new MirrorCacheException(Reason.SPI_LOAD_FAILURE, e)
                                          .withDetail("channelName: " + channelName);
        }
        
        if (results.size() == 0) {
            LOG.warn("Unable to locate suitable channelHandler.");
        }
        return results;
    }
}
