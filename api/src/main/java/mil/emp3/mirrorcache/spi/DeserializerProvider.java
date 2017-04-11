package mil.emp3.mirrorcache.spi;

import mil.emp3.mirrorcache.Deserializer;

public interface DeserializerProvider {
    
    boolean canHandle(String type);
    
    Deserializer getDeserializer();

}
