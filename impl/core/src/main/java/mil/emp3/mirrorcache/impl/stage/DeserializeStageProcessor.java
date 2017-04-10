package mil.emp3.mirrorcache.impl.stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.Deserializer;
import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.spi.DeserializerProviderFactory;

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
