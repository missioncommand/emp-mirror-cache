package mil.emp3.mirrorcache.impl.response;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MessageProcessor;

public interface ResponseProcessor extends MessageProcessor<Message> {
    void init();
    void shutdown();
}
