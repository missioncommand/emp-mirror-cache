package mil.emp3.mirrorcache;

public interface MessageProcessor<T extends Message> {

    void processMessage(T message) throws MirrorCacheException;
}
