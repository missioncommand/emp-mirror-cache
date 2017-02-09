package org.cmapi.mirrorcache.impl.spi;

import java.util.HashMap;
import java.util.Map;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.Translator;
import org.cmapi.mirrorcache.impl.translator.ContainerGeoFromTranslator;
import org.cmapi.mirrorcache.impl.translator.ContainerProtoFromTranslator;
import org.cmapi.mirrorcache.impl.translator.MilSymbolGeoFromTranslator;
import org.cmapi.mirrorcache.impl.translator.MilSymbolProtoFromTranslator;
import org.cmapi.mirrorcache.spi.TranslatorProvider;
import org.cmapi.primitives.GeoContainer;
import org.cmapi.primitives.GeoMilSymbol;
import org.cmapi.primitives.proto.CmapiProto.Container;
import org.cmapi.primitives.proto.CmapiProto.MilStdSymbol;

public class DefaultTranslatorProvider implements TranslatorProvider {
    
    final private DefaultTranslator translator;
    final private Map<String, Translator> protoTranslators;
    
    public DefaultTranslatorProvider() {
        this.translator = new DefaultTranslator();
        
        this.protoTranslators = new HashMap<>();
        
        // protoCMAPI
        this.protoTranslators.put(MilStdSymbol.class.getName(), new MilSymbolProtoFromTranslator());
        this.protoTranslators.put(Container.class.getName()   , new ContainerProtoFromTranslator());
        
        // geoCMAPI
        this.protoTranslators.put(GeoMilSymbol.class.getName(), new MilSymbolGeoFromTranslator());
        this.protoTranslators.put(GeoContainer.class.getName(), new ContainerGeoFromTranslator());
    }
    
    @Override
    public boolean canTranslateFrom(String type) {
        return protoTranslators.containsKey(type);
    }

    @Override
    public Translator getTranslator() {
        return translator;
    }

    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    private class DefaultTranslator implements Translator {
        @Override
        public void processMessage(Message message) throws MirrorCacheException {
            final String payloadType = message.getPayload().getType();
            
            final Translator translator = protoTranslators.get(payloadType);
            translator.processMessage(message);
        }
    }
}
