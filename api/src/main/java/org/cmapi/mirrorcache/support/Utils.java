package org.cmapi.mirrorcache.support;

import org.cmapi.primitives.GeoContainer;
import org.cmapi.primitives.GeoMilSymbol;

import com.google.protobuf.util.JsonFormat;

public class Utils {

    private Utils() { }
    
    static public String asString(com.google.protobuf.Message message) {
        try {
            return message.getDescriptorForType().getFullName() + " " + JsonFormat.printer()
                .omittingInsignificantWhitespace()
                .preservingProtoFieldNames()
//                .includingDefaultValueFields()
                .print(message);
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    static public String asString(GeoMilSymbol symbol) {
        return "GeoMilSymbol [name=" + symbol.getName() + ", geoId=" + symbol.getGeoId() + ", symbolStandard=" + symbol.getSymbolStandard() + ", symbolCode=" + symbol.getSymbolCode() + ", positions=" + symbol.getPositions() + ", modifiers="
                + symbol.getModifiers() + ", altitudeMode=" + symbol.getAltitudeMode() + "]";
    }
    
    static public String asString(GeoContainer container) {
        return "GeoContainer [name=" + container.getName() + "]";
    }
    
}
