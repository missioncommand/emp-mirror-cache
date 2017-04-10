package mil.emp3.mirrorcache.impl.spi;

import java.util.HashMap;
import java.util.Map;

import org.cmapi.primitives.IGeoContainer;
import org.cmapi.primitives.IGeoMilSymbol;
import org.cmapi.primitives.proto.CmapiProto.Container;
import org.cmapi.primitives.proto.CmapiProto.MilStdSymbol;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.Translator;
import mil.emp3.mirrorcache.impl.translator.ContainerGeoFromTranslator;
import mil.emp3.mirrorcache.impl.translator.ContainerProtoFromTranslator;
import mil.emp3.mirrorcache.impl.translator.MilSymbolGeoFromTranslator;
import mil.emp3.mirrorcache.impl.translator.MilSymbolProtoFromTranslator;
import mil.emp3.mirrorcache.spi.TranslatorProvider;

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
        this.protoTranslators.put(IGeoMilSymbol.class.getName(), new MilSymbolGeoFromTranslator());
        this.protoTranslators.put(IGeoContainer.class.getName(), new ContainerGeoFromTranslator());
    }
    
    @Override
    public boolean canTranslateFrom(TranslatorArguments args) {
        return protoTranslators.containsKey(args.from());
    }

    @Override
    public Translator getTranslator(TranslatorArguments args) {
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
