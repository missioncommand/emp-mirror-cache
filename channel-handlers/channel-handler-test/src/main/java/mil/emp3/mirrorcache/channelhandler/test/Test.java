package mil.emp3.mirrorcache.channelhandler.test;

import java.net.URI;
import java.util.List;

import org.cmapi.primitives.GeoMilSymbol;
import org.cmapi.primitives.proto.CmapiProto.MilStdSymbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.Deserializer;
import mil.emp3.mirrorcache.MirrorCacheClient;
import mil.emp3.mirrorcache.Serializer;
import mil.emp3.mirrorcache.Translator;
import mil.emp3.mirrorcache.Transport.TransportType;
import mil.emp3.mirrorcache.channel.ChannelHandler;
import mil.emp3.mirrorcache.spi.ChannelHandlerProviderFactory;
import mil.emp3.mirrorcache.spi.DeserializerProviderFactory;
import mil.emp3.mirrorcache.spi.MirrorCacheClientProvider;
import mil.emp3.mirrorcache.spi.MirrorCacheClientProviderFactory;
import mil.emp3.mirrorcache.spi.SerializerProviderFactory;
import mil.emp3.mirrorcache.spi.TranslatorProvider;
import mil.emp3.mirrorcache.spi.TranslatorProviderFactory;

public class Test {
    static final private Logger LOG = LoggerFactory.getLogger(Test.class);
    
    public static void main(String[] args) throws Exception {
        System.out.println("here we go\n\n");
        
        final List<ChannelHandler> handlers = ChannelHandlerProviderFactory.getChannelHandlers("inject");
        LOG.info("handlers.size(): " + handlers.size());
        for (int i = 0, len = handlers.size(); i < len; i++) {
            LOG.info("\thandler[" + i + "]: " + handlers.get(i));
        }
        

        final Deserializer deserializer = DeserializerProviderFactory.getDeserializer(MilStdSymbol.class.getName());
        LOG.info("deserializer: " + deserializer);
        
        final Translator inTranslator = TranslatorProviderFactory.getTranslator(new TranslatorProvider.TranslatorArguments() {
            @Override public String from() { return MilStdSymbol.class.getName(); }
        });
        LOG.info("inTranslator: " + inTranslator);
        
        final Translator outTranslator = TranslatorProviderFactory.getTranslator(new TranslatorProvider.TranslatorArguments() {
            @Override public String from() { return GeoMilSymbol.class.getName(); }
        });
        LOG.info("outTranslator: " + outTranslator);
        
        final Serializer serializer = SerializerProviderFactory.getSerializer(MilStdSymbol.class.getName());
        LOG.info("serializer: " + serializer);
        
        final URI endpointUri = new URI("ws://127.0.0.1:8080/mirrorcache");
        final MirrorCacheClient client = MirrorCacheClientProviderFactory.getClient(new MirrorCacheClientProvider.ClientArguments() {
            @Override public TransportType transportType() {
                return TransportType.WEBSOCKET;
            }
            @Override public URI endpoint() {
                return endpointUri;
            }
        });
        
        LOG.info("client: " + client);
        
    }
}
