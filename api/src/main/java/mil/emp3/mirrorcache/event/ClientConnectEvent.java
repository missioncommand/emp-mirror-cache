package mil.emp3.mirrorcache.event;

import mil.emp3.mirrorcache.Message;

public class ClientConnectEvent extends ClientEvent<ClientEventHandler> {
    static final public Type<ClientEventHandler> TYPE = new Type<ClientEventHandler>();

    public ClientConnectEvent(Message message) {
        super(message);
    }
    
    @Override
    public void dispatch(ClientEventHandler handler) {
        handler.onConnect(this);
    }

    @Override
    public Type<ClientEventHandler> getType() {
        return TYPE;
    }
}