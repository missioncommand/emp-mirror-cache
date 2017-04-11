package mil.emp3.mirrorcache.spi;

import mil.emp3.mirrorcache.Translator;

public interface TranslatorProvider {
    
    boolean canTranslateFrom(TranslatorArguments args);
    Translator getTranslator(TranslatorArguments args);
    
    interface TranslatorArguments {
        String from();
    }
}
