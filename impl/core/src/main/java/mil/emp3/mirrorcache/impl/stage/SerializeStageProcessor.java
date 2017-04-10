package mil.emp3.mirrorcache.impl.stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.Serializer;
import mil.emp3.mirrorcache.spi.SerializerProviderFactory;

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
