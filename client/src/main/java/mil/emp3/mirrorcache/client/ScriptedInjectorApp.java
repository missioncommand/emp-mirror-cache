package mil.emp3.mirrorcache.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.cmapi.primitives.GeoMilSymbol;
import org.cmapi.primitives.GeoPosition;
import org.cmapi.primitives.IGeoAltitudeMode.AltitudeMode;
import org.cmapi.primitives.IGeoMilSymbol;
import org.cmapi.primitives.IGeoMilSymbol.SymbolStandard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.MirrorCacheClient;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.Transport.TransportType;
import mil.emp3.mirrorcache.channel.Channel;
import mil.emp3.mirrorcache.channel.ChannelGroup;
import mil.emp3.mirrorcache.channel.Channel.Flow;
import mil.emp3.mirrorcache.channel.Channel.Type;
import mil.emp3.mirrorcache.channel.Channel.Visibility;
import mil.emp3.mirrorcache.spi.MirrorCacheClientProvider;
import mil.emp3.mirrorcache.spi.MirrorCacheClientProviderFactory;

public class ScriptedInjectorApp {
    static final private Logger LOG = LoggerFactory.getLogger(ScriptedInjectorApp.class);
    
    final private URI endpoint;
    final private List<ScriptEntry> scripts;
        
    public ScriptedInjectorApp(String endpoint, List<ScriptEntry> scripts) throws URISyntaxException {
        this.endpoint = new URI(endpoint);
        this.scripts  = scripts;

    }
    
    public void doThings() throws MirrorCacheException {
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
            for (ScriptEntry script : scripts) {
                
                /*
                 * Construct data.
                 */
                final List<IGeoMilSymbol> geoSymbols = new ArrayList<>();
                
                if (script.getType().equals("IGeoMilSymbol")) {
                    
                    for (int i = 0, len = script.getCount(); i < len; i++) {
                        
                        String name                   = script.getName() + " - " + i;
                        double latitude               = script.getLatitude() + randomDouble(-.3, .3);
                        double longitude              = script.getLongitude() + randomDouble(-.3, .3);
                        double altitude               = script.getAltitude();
                        String symbolCode             = script.getSymbolCode();
                        SymbolStandard symbolStandard = script.getSymbolStandard();
                        AltitudeMode altitudeMode     = script.getAltitudeMode();
    
                        final IGeoMilSymbol geoMilSymbol = createGeoMilSymbol(name,
                                                                              latitude,
                                                                              longitude,
                                                                              altitude,
                                                                              symbolCode,
                                                                              symbolStandard,
                                                                              altitudeMode);
                        geoSymbols.add(geoMilSymbol);
                    }
                }
                
                
                
                final boolean isChannel = script.getChannel() != null;
                
                Channel injectChannel           = null;
                ChannelGroup injectChannelGroup = null;
                
                if (isChannel) {
                    final String channelName = script.getChannel();
                    
                    // See if the channel already exists.
                    for (Channel channel : client.findChannels("*")) {
                        if (channel.getName().equals(channelName)) {
                            injectChannel = channel;
                            break;
                        }
                    }
                    
                    // Create channel if need be.
                    if (injectChannel == null) {
                        injectChannel = client.createChannel(channelName, Visibility.PUBLIC, Type.TEMPORARY);
                    }
                    
                    // Open channel for publishing.
                    injectChannel.open(Flow.BOTH, "*");
                    
                } else {
                    final String channelGroupName = script.getChannelGroup();
                    
                    // See if the channelGroup already exists.
                    for (ChannelGroup channelGroup : client.findChannelGroups("*")) {
                        if (channelGroup.getName().equals(channelGroupName)) {
                            injectChannelGroup = channelGroup;
                            break;
                        }
                    }
                    
                    // Create channelGroup if need be.
                    if (injectChannelGroup == null) {
                        injectChannelGroup = client.createChannelGroup(channelGroupName);
                    }
                    
                    // Open channelGroup for publishing.
                    injectChannelGroup.open();
                }

                if (script.getAction() != null && script.getAction().equals("publish")) {
                    // Inject data.
                    for (IGeoMilSymbol geoSymbol : geoSymbols) {
                        if (isChannel) {
                            injectChannel.publish(geoSymbol.getGeoId().toString(), IGeoMilSymbol.class, geoSymbol);
                        } else {
                            injectChannelGroup.publish(geoSymbol.getGeoId().toString(), IGeoMilSymbol.class, geoSymbol);
                        }
                    }
                }
                
                // Done..
                if (isChannel) {
                    injectChannel.close();
                } else {
                    injectChannelGroup.close();
                }
            }
            
        } finally {
            client.disconnect();
            client.shutdown();
        }
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    static private double randomDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }
    
    static private IGeoMilSymbol createGeoMilSymbol(String name,
                                                   double latitude,
                                                   double longitude,
                                                   double altitude,
                                                   String symbolCode,
                                                   SymbolStandard symbolStandard,
                                                   AltitudeMode altitudeMode) {
        final GeoPosition pos = new GeoPosition();
        pos.setLatitude(latitude);
        pos.setLongitude(longitude);
        pos.setAltitude(altitude);
        
        final GeoMilSymbol geoSymbol = new GeoMilSymbol();
        geoSymbol.setName(name);
        geoSymbol.setSymbolCode(symbolCode);
        geoSymbol.setSymbolStandard(symbolStandard);
        geoSymbol.getPositions().add(pos);
        geoSymbol.setAltitudeMode(altitudeMode);

        return geoSymbol;
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    static private class ScriptEntry {
        final private Map<String, String> properties = new HashMap<>();
        
        public ScriptEntry(Map<String, String> pairs) {
            properties.putAll(pairs);
        }
        
        public ScriptEntry(String[] pairs) {
            for (int i = 0, len = pairs.length; i < len; i++) {
                final String[] pair = pairs[i].split("=");
                properties.put(pair[0], pair[1]);
            }
        }
        
        public String getAction() {
            return properties.get("action");
        }
        public String getChannel() {
            return properties.get("channel");
        }
        public String getChannelGroup() {
            return properties.get("channelGroup");
        }
        
        public int getCount() {
            return Integer.parseInt(properties.get("count"));
        }
        
        public String getType() {
            return properties.get("type");
        }
        public String getName() {
            return properties.get("name"); 
        }
        public double getLatitude() {
            return Double.parseDouble(properties.get("latitude"));
        }
        public double getLongitude() {
            return Double.parseDouble(properties.get("longitude"));
        }
        public double getAltitude() {
            return Double.parseDouble(properties.get("altitude"));
        }
        public String getSymbolCode() {
            return properties.get("symbolCode");
        }
        public SymbolStandard getSymbolStandard() {
            return SymbolStandard.valueOf(properties.get("symbolStandard"));
        }
        public AltitudeMode getAltitudeMode() {
            return AltitudeMode.valueOf(properties.get("altitudeMode"));
        }

        @Override
        public String toString() {
            return "ScriptEntry [getChannel()=" + getChannel() + ", getChannelGroup()=" + getChannelGroup() + ", getCount()=" + getCount() + ", getType()=" + getType() + ", getName()=" + getName() + ", getLatitude()=" + getLatitude()
                    + ", getLongitude()=" + getLongitude() + ", getAltitude()=" + getAltitude() + ", getSymbolCode()=" + getSymbolCode() + ", getSymbolStandard()=" + getSymbolStandard() + ", getAltitudeMode()=" + getAltitudeMode() + "]";
        }
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    static public void main(String[] args) {
        System.out.println("here we go\n\n");
        
        final List<ScriptEntry> scripts = new ArrayList<>();
        
        final String endpoint = "ws://127.0.0.1:8080/mirrorcache?agent=inject";
        LOG.info("endpoint: " + endpoint);
        
        if (args.length == 1) { // via file
            final String filename = args[0];
            
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().length() == 0) {
                        continue;
                    }
                    final String[] pairs = line.split("\\|");
                    
                    scripts.add(new ScriptEntry(pairs));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        } else { //default
            final Map<String, String> pairs = new HashMap<>();
            pairs.put("channel"       , "inject");
            pairs.put("count"         , "1");
            pairs.put("type"          , "IGeoMilSymbol");
            pairs.put("name"          , "Unit 1");
            pairs.put("latitude"      , "1.2");
            pairs.put("longitude"     , "3.4");
            pairs.put("altitude"      , "10");
            pairs.put("symbolCode"    , "SUGP---------XX");
            pairs.put("symbolStandard", SymbolStandard.MIL_STD_2525C.toString());
            pairs.put("altitudeMode"  , AltitudeMode.RELATIVE_TO_GROUND.toString());
            
            scripts.add(new ScriptEntry(pairs));
        }

        LOG.info("ScriptEntries:");
        for (ScriptEntry script : scripts) {
            LOG.info("" + script);
        }
        
        try {
            final ScriptedInjectorApp injector = new ScriptedInjectorApp(endpoint, scripts);
            injector.doThings();
            
        } catch (URISyntaxException | MirrorCacheException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
