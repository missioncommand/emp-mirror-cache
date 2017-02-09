package org.cmapi.mirrorcache.event;

import org.cmapi.mirrorcache.Message;

public abstract class ClientEvent<T extends ClientEventHandler> extends MirrorCacheEvent<T> {
    
    final private Message message;
    
    public ClientEvent(Message message) {
        this.message = message;
    }
    
    public Message getMessage() {
        return message;
    }
}
