package org.cmapi.mirrorcache.impl.translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.Payload;
import org.cmapi.mirrorcache.Translator;
import org.cmapi.primitives.IGeoMilSymbol;
import org.cmapi.primitives.IGeoPosition;
import org.cmapi.primitives.proto.CmapiProto.AltitudeMode;
import org.cmapi.primitives.proto.CmapiProto.GeoPosition;
import org.cmapi.primitives.proto.CmapiProto.MilStdSymbol;
import org.cmapi.primitives.proto.CmapiProto.SymbolStandard;


public class MilSymbolGeoFromTranslator implements Translator {
    
    @Override
    public void processMessage(Message message) throws MirrorCacheException {
        final IGeoMilSymbol symbol     = message.getPayload(IGeoMilSymbol.class).getData();
        final MilStdSymbol newSymbol = translate(symbol);
     
        message.setPayload(new Payload<>(newSymbol.getGeoId(), MilStdSymbol.class.getName(), newSymbol));
    }
    
    public MilStdSymbol translate(IGeoMilSymbol symbol) {
        final List<GeoPosition> positions = new ArrayList<>();
        for (Iterator<IGeoPosition> iter = symbol.getPositions().iterator(); iter.hasNext(); ) {
            final IGeoPosition pos = iter.next();
            
            positions.add(GeoPosition.newBuilder()
                .setLatitude(pos.getLatitude())
                .setLongitude(pos.getLongitude())
                .setAltitude(pos.getAltitude())
            .build());
        }
        
        final Map<String, String> modifiers = new HashMap<>();
        for (org.cmapi.primitives.IGeoMilSymbol.Modifier m : symbol.getModifiers().keySet()) {
            modifiers.put(m.name(), symbol.getModifiers().get(m).toString());
        }
        
        final MilStdSymbol newSymbol = MilStdSymbol.newBuilder()
                .setGeoId(symbol.getGeoId().toString())
                .setName(symbol.getName())
                .setSymbolCode(symbol.getSymbolCode())
                .setSymbolStandard(SymbolStandard.valueOf(symbol.getSymbolStandard().name()))
                .setAltitudeMode(AltitudeMode.valueOf(symbol.getAltitudeMode().name()))
                .addAllPosition(positions)
                .putAllModifier(modifiers)
                //TODO fillStyle
                //TODO strokeStyle
                //TODO labelStyle
            .build();
        
        return newSymbol;
    }

}
