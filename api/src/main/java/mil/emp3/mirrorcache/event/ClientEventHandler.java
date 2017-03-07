package mil.emp3.mirrorcache.event;

public interface ClientEventHandler extends EventHandler {
    
    void onMessage(ClientMessageEvent event);
    void onConnect(ClientConnectEvent event);
    void onDisconnect(ClientDisconnectEvent event);

}
