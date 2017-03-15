package mil.emp3.mirrorcache.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.cmapi.primitives.GeoContainer;
import org.cmapi.primitives.GeoMilSymbol;
import org.cmapi.primitives.GeoPosition;
import org.cmapi.primitives.IGeoContainer;
import org.cmapi.primitives.IGeoMilSymbol;
import org.cmapi.primitives.IGeoAltitudeMode.AltitudeMode;
import org.cmapi.primitives.IGeoMilSymbol.Modifier;
import org.cmapi.primitives.IGeoMilSymbol.SymbolStandard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.MirrorCacheClient;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.Transport.TransportType;
import mil.emp3.mirrorcache.channel.Channel;
import mil.emp3.mirrorcache.channel.Channel.Flow;
import mil.emp3.mirrorcache.spi.MirrorCacheClientProvider;
import mil.emp3.mirrorcache.spi.MirrorCacheClientProviderFactory;

public class InjectorApp {
    static final private Logger LOG = LoggerFactory.getLogger(InjectorApp.class);
    
    final private URI endpoint;
    final private String channelName;
    final private List<IGeoContainer> geoContainers;
    
    public InjectorApp(String endpoint, String channelName, int numContainers, int numSymbolsPerContainer) throws URISyntaxException {
        this.endpoint      = new URI(endpoint);
        this.channelName   = channelName;
        this.geoContainers = new ArrayList<>();
     
        /*
         * Construct message objects. 
         */
        for (int i = 0; i < numContainers; i++) {
            final IGeoContainer geoContainer = createGeoContainer("Container #" + i);
            
            for (int j = 0; j < numSymbolsPerContainer; j++) {
                geoContainer.getChildren().add(createGeoMilSymbol("Unit #" + j));
            }
            
            geoContainers.add(geoContainer);
        }
    }
    
    public void doInject() throws MirrorCacheException {
        /*
         * Create and initialize client.
         */
        final MirrorCacheClient client = MirrorCacheClientProviderFactory.getClient(new MirrorCacheClientProvider.ClientArguments() {
            @Override public TransportType transportType() {
                return TransportType.WEBSOCKET;
            }
            @Override public URI endpoint() {
                return endpoint;
            }
        });
        client.init();
        client.connect();
        
        try {
            /*
             * See if the channel already exists.
             */
            Channel injectChannel = null;
            for (Channel channel : client.findChannels("*")) {
                if (channel.getName().equals(channelName)) {
                    injectChannel = channel;
                    break;
                }
            }
            
            /*
             * Create channel if need be.
             */
            if (injectChannel == null) {
                injectChannel = client.createChannel(channelName, Channel.Visibility.PUBLIC, Channel.Type.TEMPORARY);
            }
            
            /*
             * Open channel for publishing.
             */
            injectChannel.open(Flow.EGRESS, "*");
            
            /*
             * Inject data.
             */
            for (IGeoContainer geoContainer : geoContainers) {
                injectChannel.publish(geoContainer.getGeoId().toString(), IGeoContainer.class, geoContainer);                
            }
            
            /*
             * We're done..
             */
            injectChannel.close();
            
            client.shutdown();
            
        } catch (MirrorCacheException e) {
            e.printStackTrace();
        }
    }
    
    private static IGeoContainer createGeoContainer(String name) {
        final IGeoContainer geoContainer = new GeoContainer();
        geoContainer.setName(name);
        
        return geoContainer;
    }
    
    private static IGeoMilSymbol createGeoMilSymbol(String name) {
        /*
         * Create symbol to publish
         */
        final GeoPosition pos = new GeoPosition();
        pos.setLatitude(1.2);
        pos.setLongitude(3.4);
        pos.setAltitude(10.);
        
        final IGeoMilSymbol geoSymbol = new GeoMilSymbol();
        geoSymbol.setName(name);
        geoSymbol.setSymbolCode("SUGP---------XX");
        geoSymbol.setSymbolStandard(SymbolStandard.MIL_STD_2525C);
        geoSymbol.setAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
        geoSymbol.getPositions().add(pos);
        geoSymbol.getModifiers().put(Modifier.UNIQUE_DESIGNATOR_1, "Maintenance Recovery Theater");
        
        return geoSymbol;
    }
    
    public static void main(String[] args) {
        System.out.println("here we go\n\n");
        
        String endpoint            = "ws://127.0.0.1:8080/mirrorcache?agent=inject";
        String channelName         = "inject";
        int numContainers          = 1;
        int numSymbolsPerContainer = 1;
        
        if (args.length == 4) {
            endpoint                = args[0];
            channelName             = args[1];
            numContainers           = Integer.parseInt(args[2]);
            numSymbolsPerContainer  = Integer.parseInt(args[3]);
        }
        LOG.info("endpoint: " + endpoint);
        LOG.info("channelName: " + channelName);
        LOG.info("numContainers: " + numContainers);
        LOG.info("numSymbolsPerContainer: " + numSymbolsPerContainer);
        
        try {
            new InjectorApp(endpoint, channelName, numContainers, numSymbolsPerContainer)
                    .doInject();
            
        } catch (URISyntaxException | MirrorCacheException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
