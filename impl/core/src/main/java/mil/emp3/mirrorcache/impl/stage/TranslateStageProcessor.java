package mil.emp3.mirrorcache.impl.stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.Translator;
import mil.emp3.mirrorcache.spi.TranslatorProvider;
import mil.emp3.mirrorcache.spi.TranslatorProviderFactory;

public class TranslateStageProcessor extends StageProcessor<Message> {
    static final private Logger LOG = LoggerFactory.getLogger(TranslateStageProcessor.class);
    
    public TranslateStageProcessor() {
        super(Stage.TRANSLATE);
    }
    
    @Override
    public void processMessage(final Message message) throws MirrorCacheException {
        LOG.trace("processMessage()");
        
        if (message.hasPayload()) {
            final Translator translator = TranslatorProviderFactory.getTranslator(new TranslatorProvider.TranslatorArguments() {
                @Override public String from() {
                    return message.getPayload().getType();
                }
            });
            translator.processMessage(message);
        }
    }
}
