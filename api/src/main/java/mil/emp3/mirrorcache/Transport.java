package mil.emp3.mirrorcache;

public interface Transport {

    public enum TransportType {
        WEBSOCKET,
        WEBSOCKET_ANNOTATED,
        WEBSOCKET_PROGRAMMATIC,
        ;
    }
    
    void connect() throws MirrorCacheException;
    void disconnect() throws MirrorCacheException;
    void send(Message message) throws MirrorCacheException;
}
