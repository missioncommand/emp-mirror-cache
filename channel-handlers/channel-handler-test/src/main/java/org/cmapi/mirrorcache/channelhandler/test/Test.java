package org.cmapi.mirrorcache.channelhandler.test;

import java.net.URI;
import java.util.List;

import org.cmapi.mirrorcache.Deserializer;
import org.cmapi.mirrorcache.MirrorCacheClient;
import org.cmapi.mirrorcache.Serializer;
import org.cmapi.mirrorcache.Translator;
import org.cmapi.mirrorcache.channel.ChannelHandler;
import org.cmapi.mirrorcache.impl.spi.ChannelHandlerProviderFactory;
import org.cmapi.mirrorcache.impl.spi.DeserializerProviderFactory;
import org.cmapi.mirrorcache.impl.spi.MirrorCacheClientProviderFactory;
import org.cmapi.mirrorcache.impl.spi.SerializerProviderFactory;
import org.cmapi.mirrorcache.impl.spi.TranslatorProviderFactory;
import org.cmapi.primitives.GeoMilSymbol;
import org.cmapi.primitives.proto.CmapiProto.MilStdSymbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        
        final Translator inTranslator = TranslatorProviderFactory.getTranslator(MilStdSymbol.class.getName());
        LOG.info("inTranslator: " + inTranslator);
        
        final Translator outTranslator = TranslatorProviderFactory.getTranslator(GeoMilSymbol.class.getName());
        LOG.info("outTranslator: " + outTranslator);
        
        final Serializer serializer = SerializerProviderFactory.getSerializer(MilStdSymbol.class.getName());
        LOG.info("serializer: " + serializer);
        
        final MirrorCacheClient client = MirrorCacheClientProviderFactory.getClient(new URI("ws://127.0.0.1:8080/mirrorcache"));
        LOG.info("client: " + client);
        
    }
}
