package org.cmapi.mirrorcache.event;

import org.cmapi.mirrorcache.Message;

public class ClientDisconnectEvent extends ClientEvent<ClientEventHandler> {
    static final public Type<ClientEventHandler> TYPE = new Type<ClientEventHandler>();

    public ClientDisconnectEvent(Message message) {
        super(message);
    }
    
    @Override
    public void dispatch(ClientEventHandler handler) {
        handler.onDisconnect(this);
    }

    @Override
    public Type<ClientEventHandler> getType() {
        return TYPE;
    }
}