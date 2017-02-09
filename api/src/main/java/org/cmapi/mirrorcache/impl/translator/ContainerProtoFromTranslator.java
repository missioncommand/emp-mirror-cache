package org.cmapi.mirrorcache.impl.translator;

import java.util.ArrayList;
import java.util.List;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.Payload;
import org.cmapi.mirrorcache.Translator;
import org.cmapi.primitives.GeoContainer;
import org.cmapi.primitives.IGeoBase;
import org.cmapi.primitives.IGeoContainer;
import org.cmapi.primitives.proto.CmapiProto.Container;
import org.cmapi.primitives.proto.CmapiProto.OneOfFeature;

public class ContainerProtoFromTranslator implements Translator {

    private MilSymbolProtoFromTranslator milSymbolProtoTranslator = new MilSymbolProtoFromTranslator();
    
    @Override
    public void processMessage(Message message) throws MirrorCacheException {
        final Container container        = message.getPayload(Container.class).getData();
        final IGeoContainer newContainer = translate(container);
        
        message.setPayload(new Payload<>(newContainer.getGeoId().toString(), IGeoContainer.class.getName(), newContainer));
    }

    public IGeoContainer translate(Container container) {
        
        final List<IGeoBase> features = new ArrayList<>();
        for (OneOfFeature feature : container.getFeatureList()) {
            switch (feature.getFeatureCase()) {
                case SYMBOL: {
                    features.add(milSymbolProtoTranslator.translate(feature.getSymbol()));
                    break;
                }
                case CIRCLE: {
                    throw new IllegalStateException("not implemented");
                }
                case FEATURE_NOT_SET: {
                    throw new IllegalStateException("Feature not set");
                }
            }
        }
        
        final IGeoContainer newContainer = new GeoContainer();
        newContainer.setName(container.getName());
        newContainer.getChildren().addAll(features);
        
        return newContainer;
    }

}
