package org.cmapi.mirrorcache.spi;

import org.cmapi.mirrorcache.Deserializer;

public interface DeserializerProvider {
    
    boolean canHandle(String type);
    
    Deserializer getDeserializer();

}
