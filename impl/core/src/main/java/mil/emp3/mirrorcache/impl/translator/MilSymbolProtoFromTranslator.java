package mil.emp3.mirrorcache.impl.translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.cmapi.primitives.GeoMilSymbol;
import org.cmapi.primitives.IGeoAltitudeMode.AltitudeMode;
import org.cmapi.primitives.IGeoMilSymbol;
import org.cmapi.primitives.IGeoMilSymbol.Modifier;
import org.cmapi.primitives.IGeoMilSymbol.SymbolStandard;
import org.cmapi.primitives.IGeoPosition;
import org.cmapi.primitives.proto.CmapiProto.GeoPosition;
import org.cmapi.primitives.proto.CmapiProto.MilStdSymbol;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.Payload;
import mil.emp3.mirrorcache.Translator;


public class MilSymbolProtoFromTranslator implements Translator {
    
    @Override
    public void processMessage(Message message) throws MirrorCacheException {
        final MilStdSymbol symbol     = message.getPayload(MilStdSymbol.class).getData();
        final IGeoMilSymbol newSymbol = translate(symbol);
     
        message.setPayload(new Payload<>(newSymbol.getGeoId().toString(), IGeoMilSymbol.class.getName(), newSymbol));
    }
    
    public IGeoMilSymbol translate(MilStdSymbol symbol) {
        final List<IGeoPosition> positions = new ArrayList<>();
        for (GeoPosition pos : symbol.getPositionList()) {
            
            final IGeoPosition position = new org.cmapi.primitives.GeoPosition();
            position.setLongitude(pos.getLongitude());
            position.setLatitude(pos.getLatitude());
            position.setAltitude(pos.getAltitude());
            
            positions.add(position);
        }
        
        final HashMap<Modifier, String> modifiers = new HashMap<>();
        for (String modifier : symbol.getModifierMap().keySet()) {
            
            modifiers.put(Modifier.valueOf(modifier), symbol.getModifierMap().get(modifier));
        }
        
        final IGeoMilSymbol newSymbol = new GeoMilSymbol();
        newSymbol.setGeoId(UUID.fromString(symbol.getGeoId()));
        newSymbol.setName(symbol.getName());
        newSymbol.setSymbolCode(symbol.getSymbolCode());
        newSymbol.setSymbolStandard(SymbolStandard.valueOf(symbol.getSymbolStandard().name()));
        newSymbol.setAltitudeMode(AltitudeMode.valueOf(symbol.getAltitudeMode().name()));
        newSymbol.setPositions(positions);
        newSymbol.setModifiers(modifiers);
    
        return newSymbol;
    }
}
