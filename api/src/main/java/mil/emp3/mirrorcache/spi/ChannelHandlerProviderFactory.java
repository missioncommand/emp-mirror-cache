package mil.emp3.mirrorcache.spi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.channel.ChannelHandler;

public class ChannelHandlerProviderFactory {
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
            throw new MirrorCacheException(Reason.SPI_LOAD_FAILURE, e).withDetail("channelName: " + channelName);
        }
        
        return results;
    }
}
