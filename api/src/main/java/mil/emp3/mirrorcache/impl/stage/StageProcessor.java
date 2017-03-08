package mil.emp3.mirrorcache.impl.stage;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MessageProcessor;

public abstract class StageProcessor<T extends Message> implements MessageProcessor<T> {

    final private Stage stage;
    
    public StageProcessor(Stage stage) {
        this.stage = stage;
    }
    
    public Stage getStage() {
        return stage;
    }
}
