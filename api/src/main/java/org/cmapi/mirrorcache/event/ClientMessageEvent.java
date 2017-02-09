package org.cmapi.mirrorcache.event;

import org.cmapi.mirrorcache.Message;

public class ClientMessageEvent extends ClientEvent<ClientEventHandler> {
    static final public Type<ClientEventHandler> TYPE = new Type<ClientEventHandler>();

    public ClientMessageEvent(Message message) {
        super(message);
    }
    
    @Override
    public void dispatch(ClientEventHandler handler) {
        handler.onMessage(this);
    }

    @Override
    public Type<ClientEventHandler> getType() {
        return TYPE;
    }
}
