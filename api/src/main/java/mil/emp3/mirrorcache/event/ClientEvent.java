package mil.emp3.mirrorcache.event;

import mil.emp3.mirrorcache.Message;

public abstract class ClientEvent<T extends ClientEventHandler> extends MirrorCacheEvent<T> {
    
    final private Message message;
    
    public ClientEvent(Message message) {
        this.message = message;
    }
    
    public Message getMessage() {
        return message;
    }
}
