package mil.emp3.mirrorcache.impl.stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.Translator;
import mil.emp3.mirrorcache.impl.spi.TranslatorProviderFactory;

public class TranslateStageProcessor extends StageProcessor<Message> {
    static final private Logger LOG = LoggerFactory.getLogger(TranslateStageProcessor.class);
    
    public TranslateStageProcessor() {
        super(Stage.TRANSLATE);
    }
    
    @Override
    public void processMessage(Message message) throws MirrorCacheException {
        LOG.trace("processMessage()");
        
        if (message.hasPayload()) {
            final String payloadType = message.getPayload().getType();
            
            final Translator translator = TranslatorProviderFactory.getTranslator(payloadType);
            translator.processMessage(message);
        }
    }
}
