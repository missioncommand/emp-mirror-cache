package org.cmapi.mirrorcache.spi;

import org.cmapi.mirrorcache.Translator;

public interface TranslatorProvider {

    boolean canTranslateFrom(String type);
    
    Translator getTranslator();
}
