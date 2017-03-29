package mil.emp3.mirrorcache;

import java.util.List;
import java.util.concurrent.Future;

import mil.emp3.mirrorcache.channel.Channel;
import mil.emp3.mirrorcache.channel.ChannelGroup;
import mil.emp3.mirrorcache.event.EventHandler;
import mil.emp3.mirrorcache.event.EventRegistration;
import mil.emp3.mirrorcache.event.MirrorCacheEvent;

public interface MirrorCacheClient {

//TODO    void init(List<ClientPolicy> policies) [PublishPolicy:ON_DEMAND:INSTANT:WHEN_READY]
    void init();
    void shutdown() throws MirrorCacheException;
    
    void connect() throws MirrorCacheException;
    void disconnect() throws MirrorCacheException;

    /** Register to receive client events. */
    <T extends EventHandler> EventRegistration on(MirrorCacheEvent.Type<T> type, T handler);
    
    ClientInfo getClientInfo();
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    ChannelGroup createChannelGroup(String name) throws MirrorCacheException;
    void deleteChannelGroup(String name) throws MirrorCacheException;
    List<ChannelGroup> findChannelGroups(String filter) throws MirrorCacheException;
    
    Future<ChannelGroup> createChannelGroupAsync(String name);
    Future<Void> deleteChannelGroupAsync(String name);
    Future<List<ChannelGroup>> findChannelGroupsAsync(String filter);
    
    Channel createChannel(String name, Channel.Visibility visibility, Channel.Type type) throws MirrorCacheException;
    void deleteChannel(String name) throws MirrorCacheException;
    List<Channel> findChannels(String filter) throws MirrorCacheException;
    
    Future<Channel> createChannelAsync(String name, Channel.Visibility visibility, Channel.Type type);
    Future<Void> deleteChannelAsync(String name);
    Future<List<Channel>> findChannelsAsync(String filter);
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    public interface ClientInfo {
        String clientId();
    }
}
