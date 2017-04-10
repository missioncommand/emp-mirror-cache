package mil.emp3.mirrorcache.impl.translator;

import java.util.ArrayList;
import java.util.List;

import org.cmapi.primitives.IGeoBase;
import org.cmapi.primitives.IGeoContainer;
import org.cmapi.primitives.IGeoMilSymbol;
import org.cmapi.primitives.proto.CmapiProto.Container;
import org.cmapi.primitives.proto.CmapiProto.MilStdSymbol;
import org.cmapi.primitives.proto.CmapiProto.OneOfFeature;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.Payload;
import mil.emp3.mirrorcache.Translator;

public class ContainerGeoFromTranslator implements Translator {

    private MilSymbolGeoFromTranslator milSymbolGeoTranslator = new MilSymbolGeoFromTranslator();
    
    @Override
    public void processMessage(Message message) throws MirrorCacheException {
        final IGeoContainer container = message.getPayload(IGeoContainer.class).getData();
        final Container newContainer  = translate(container);
        
        message.setPayload(new Payload<>(newContainer.getGeoId(), Container.class.getName(), newContainer));
    }
    
    public Container translate(IGeoContainer container) {
        final List<OneOfFeature> features = new ArrayList<>();
        for (IGeoBase child : container.getChildren()) {
            if (child instanceof IGeoMilSymbol) {
                final IGeoMilSymbol geoSymbol = (IGeoMilSymbol) child;
                
                final MilStdSymbol symbol = milSymbolGeoTranslator.translate(geoSymbol);
                features.add(OneOfFeature.newBuilder().setSymbol(symbol).build());
            }
        }
        
        final Container newContainer = Container.newBuilder()
                .setName(container.getName())
                .addAllFeature(features)
            .build();

        return newContainer;
    }
}
