package mil.emp3.mirrorcache.spi;

import mil.emp3.mirrorcache.Serializer;

public interface SerializerProvider {

    boolean canHandle(String type);
    
    Serializer getSerializer();
}
