package org.cmapi.mirrorcache.impl.stage;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MessageProcessor;

public abstract class StageProcessor<T extends Message> implements MessageProcessor<T> {

    final private Stage stage;
    
    public StageProcessor(Stage stage) {
        this.stage = stage;
    }
    
    public Stage getStage() {
        return stage;
    }
}
