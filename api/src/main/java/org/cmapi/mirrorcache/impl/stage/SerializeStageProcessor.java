package org.cmapi.mirrorcache.impl.stage;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.Serializer;
import org.cmapi.mirrorcache.impl.spi.SerializerProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializeStageProcessor extends StageProcessor<Message> {
    static final private Logger LOG = LoggerFactory.getLogger(SerializeStageProcessor.class);
    
    public SerializeStageProcessor() {
        super(Stage.SERIALIZE);
    }
    
    @Override
    public void processMessage(Message message) throws MirrorCacheException {
        LOG.trace("processMessage()");
        
        if (message.hasPayload()) {
            final String payloadType = message.getPayload().getType();
            
            final Serializer serializer = SerializerProviderFactory.getSerializer(payloadType);
            serializer.processMessage(message);
        }
    }
}
