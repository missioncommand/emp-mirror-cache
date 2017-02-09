package org.cmapi.mirrorcache.impl.response;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MessageProcessor;

public interface ResponseProcessor extends MessageProcessor<Message> {
    void init();
    void shutdown();
}
