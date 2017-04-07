package mil.emp3.mirrorcache.impl;

import org.cmapi.primitives.GeoContainer;
import org.cmapi.primitives.GeoMilSymbol;

public class Utils {

    private Utils() { }
    
    static public String asString(com.google.protobuf.MessageLite message) {
        return message.toString();
    }
    
    static public String asString(com.google.protobuf.Message message) {
        return message.toString();
//        try {
//            return message.getDescriptorForType().getFullName() + " " + JsonFormat.printer()
//                .omittingInsignificantWhitespace()
//                .preservingProtoFieldNames()
////                .includingDefaultValueFields()
//                .print(message);
//            
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }
    
    static public String asString(GeoMilSymbol symbol) {
        return "GeoMilSymbol [name=" + symbol.getName() + ", geoId=" + symbol.getGeoId() + ", symbolStandard=" + symbol.getSymbolStandard() + ", symbolCode=" + symbol.getSymbolCode() + ", positions=" + symbol.getPositions() + ", modifiers="
                + symbol.getModifiers() + ", altitudeMode=" + symbol.getAltitudeMode() + "]";
    }
    
    static public String asString(GeoContainer container) {
        return "GeoContainer [name=" + container.getName() + "]";
    }
    
}
