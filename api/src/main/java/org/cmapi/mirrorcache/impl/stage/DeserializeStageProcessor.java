package org.cmapi.mirrorcache.impl.stage;

import org.cmapi.mirrorcache.Deserializer;
import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.impl.spi.DeserializerProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeserializeStageProcessor extends StageProcessor<Message> {
    static final private Logger LOG = LoggerFactory.getLogger(DeserializeStageProcessor.class);
    
    public DeserializeStageProcessor() {
        super(Stage.DESERIALIZE);
    }
    
    @Override
    public void processMessage(Message message) throws MirrorCacheException {
        LOG.trace("processMessage()");
        
        if (message.hasPayload()) {
            final String payloadType = message.getPayload().getType();
            
            final Deserializer deserializer = DeserializerProviderFactory.getDeserializer(payloadType);
            deserializer.processMessage(message);
        }
    }
}
