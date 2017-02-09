package org.cmapi.mirrorcache.impl.stage;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.Translator;
import org.cmapi.mirrorcache.impl.spi.TranslatorProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
