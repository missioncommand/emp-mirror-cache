package org.cmapi.mirrorcache;

public interface Transport {

    void connect() throws MirrorCacheException;
    void disconnect() throws MirrorCacheException;
    void send(Message message) throws MirrorCacheException;
}
