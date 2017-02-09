package org.cmapi.mirrorcache.spi;

import org.cmapi.mirrorcache.Serializer;

public interface SerializerProvider {

    boolean canHandle(String type);
    
    Serializer getSerializer();
}
