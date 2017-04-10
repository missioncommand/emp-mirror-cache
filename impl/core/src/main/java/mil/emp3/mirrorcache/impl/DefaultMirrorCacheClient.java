package mil.emp3.mirrorcache.impl;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Future;

import mil.emp3.mirrorcache.MessageDispatcher;
import mil.emp3.mirrorcache.MirrorCacheClient;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.Transport;
import mil.emp3.mirrorcache.Transport.TransportType;
import mil.emp3.mirrorcache.channel.Channel;
import mil.emp3.mirrorcache.channel.ChannelGroup;
import mil.emp3.mirrorcache.event.ClientConnectEvent;
import mil.emp3.mirrorcache.event.ClientEventHandlerAdapter;
import mil.emp3.mirrorcache.event.EventHandler;
import mil.emp3.mirrorcache.event.EventRegistration;
import mil.emp3.mirrorcache.event.MirrorCacheEvent;
import mil.emp3.mirrorcache.impl.request.CreateChannelGroupRequestProcessor;
import mil.emp3.mirrorcache.impl.request.CreateChannelRequestProcessor;
import mil.emp3.mirrorcache.impl.request.DeleteChannelGroupRequestProcessor;
import mil.emp3.mirrorcache.impl.request.DeleteChannelRequestProcessor;
import mil.emp3.mirrorcache.impl.request.FindChannelGroupsRequestProcessor;
import mil.emp3.mirrorcache.impl.request.FindChannelsRequestProcessor;
import mil.emp3.mirrorcache.impl.request.GetClientInfoRequestProcessor;
import mil.emp3.mirrorcache.spi.MirrorCacheClientProvider;
import mil.emp3.mirrorcache.spi.TransportProvider;
import mil.emp3.mirrorcache.spi.TransportProviderFactory;

public class DefaultMirrorCacheClient implements MirrorCacheClient {

    volatile private boolean isInitialized;
    
    private ClientInfo clientId;
    private EventRegistration clientEventRegistration;
    
    final private MessageDispatcher messageDispatcher;
    final private Transport transport;
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //

    public DefaultMirrorCacheClient(final MirrorCacheClientProvider.ClientArguments args) {
        this.messageDispatcher = new DefaultMessageDispatcher(this);
        try {
            this.transport = TransportProviderFactory.getTransport(new TransportProvider.TransportArguments() {
                @Override public URI endpoint() {
                    return args.endpoint();
                }
                @Override public TransportType type() {
                    return args.transportType();
                }
                @Override public MessageDispatcher messageDispatcher() {
                    return messageDispatcher;
                }
            });
            
        } catch (MirrorCacheException e) {
            throw new IllegalStateException(e);
        }
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    @Override
    public void init() {
        if (!isInitialized) {
            synchronized(this) {
                if (!isInitialized) {

                    getMessageDispatcher().init();
                    
                    this.clientEventRegistration = getMessageDispatcher().on(ClientConnectEvent.TYPE, new ClientEventHandlerAdapter() {
                        @Override public void onConnect(ClientConnectEvent event) {
                            /*
                             * Retrieve 'clientInfo' from server.
                             */
                            try {
                                final ClientInfo clientInfo = getMessageDispatcher().getRequestProcessor(GetClientInfoRequestProcessor.class).executeSync(MessageBuilder.buildGetClientInfoMessage());
                                DefaultMirrorCacheClient.this.clientId = clientInfo;

                            } catch (MirrorCacheException e) {
                                throw new IllegalStateException("Unable to retrieve clientInfo: " + e.getMessage(), e);
                            }
                        }
                    });
                    
                    isInitialized = true;
                } 
            }
        }
    }
    
    @Override
    public void shutdown() throws MirrorCacheException {
        initCheck();
        
        if (isInitialized) {
            synchronized(this) {
                if (isInitialized) {
                    /*
                     * We are no longer interested in receiving ClientConnectEvent events.
                     */
                    if (clientEventRegistration != null) {
                        clientEventRegistration.removeHandler();
                        clientEventRegistration = null;
                    }
                    
                    disconnect();
                    getMessageDispatcher().shutdown();
                    
                    isInitialized = false;
                }
            }
        }
    }
    
    @Override
    public void connect() throws MirrorCacheException {
        initCheck();
        getTransport().connect();
    }

    @Override
    public void disconnect() throws MirrorCacheException {
        initCheck();
        getTransport().disconnect();
    }
    
    @Override
    public <T extends EventHandler> EventRegistration on(MirrorCacheEvent.Type<T> type, T handler) {
        initCheck();
        return getMessageDispatcher().on(type, handler);
    }

    @Override
    public ClientInfo getClientInfo() {
        return clientId;
    }
    
    @Override
    public Channel createChannel(final String name, final Channel.Visibility visibility, final Channel.Type type) throws MirrorCacheException {
        initCheck();
        final Channel channel = getMessageDispatcher().getRequestProcessor(CreateChannelRequestProcessor.class).executeSync(MessageBuilder.buildCreateChannelMessage(name, visibility, type));
        return channel;
    }
    
    @Override
    public Future<Channel> createChannelAsync(final String name, final Channel.Visibility visibility, final Channel.Type type) {
        initCheck();
        final Future<Channel> futureChannel = getMessageDispatcher().getRequestProcessor(CreateChannelRequestProcessor.class).executeAsync(MessageBuilder.buildCreateChannelMessage(name, visibility, type));
        return futureChannel;
    }
    
    @Override
    public void deleteChannel(String name) throws MirrorCacheException {
        initCheck();
        getMessageDispatcher().getRequestProcessor(DeleteChannelRequestProcessor.class).executeSync(MessageBuilder.buildDeleteChannelMessage(name));
    }

    @Override
    public Future<Void> deleteChannelAsync(String name) {
        initCheck();
        final Future<Void> futureVoid = getMessageDispatcher().getRequestProcessor(DeleteChannelRequestProcessor.class).executeAsync(MessageBuilder.buildDeleteChannelMessage(name));
        return futureVoid;
    }
    
    @Override
    public List<Channel> findChannels(String filter) throws MirrorCacheException {
        initCheck();
        final List<Channel> channels = getMessageDispatcher().getRequestProcessor(FindChannelsRequestProcessor.class).executeSync(MessageBuilder.buildFindChannelsMessage(filter));
        return channels;
    }
    
    @Override
    public Future<List<Channel>> findChannelsAsync(String filter) {
        initCheck();
        final Future<List<Channel>> futureChannels = getMessageDispatcher().getRequestProcessor(FindChannelsRequestProcessor.class).executeAsync(MessageBuilder.buildFindChannelsMessage(filter));
        return futureChannels;
    }
    
    @Override
    public ChannelGroup createChannelGroup(String name) throws MirrorCacheException {
        initCheck();
        final ChannelGroup channelGroup = getMessageDispatcher().getRequestProcessor(CreateChannelGroupRequestProcessor.class).executeSync(MessageBuilder.buildCreateChannelGroupMessage(name));
        return channelGroup;
    }
    
    @Override
    public Future<ChannelGroup> createChannelGroupAsync(String name) {
        initCheck();
        final Future<ChannelGroup> futureChannelGroup = getMessageDispatcher().getRequestProcessor(CreateChannelGroupRequestProcessor.class).executeAsync(MessageBuilder.buildCreateChannelGroupMessage(name));
        return futureChannelGroup;
    }
    
    @Override
    public void deleteChannelGroup(String name) throws MirrorCacheException {
        initCheck();
        getMessageDispatcher().getRequestProcessor(DeleteChannelGroupRequestProcessor.class).executeSync(MessageBuilder.buildDeleteChannelGroupMessage(name));
    }
    
    @Override
    public Future<Void> deleteChannelGroupAsync(String name) {
        initCheck();
        final Future<Void> futureVoid = getMessageDispatcher().getRequestProcessor(DeleteChannelGroupRequestProcessor.class).executeAsync(MessageBuilder.buildDeleteChannelGroupMessage(name));
        return futureVoid;
    }
    
    @Override
    public List<ChannelGroup> findChannelGroups(String filter) throws MirrorCacheException {
        initCheck();
        final List<ChannelGroup> channelGroups = getMessageDispatcher().getRequestProcessor(FindChannelGroupsRequestProcessor.class).executeSync(MessageBuilder.buildFindChannelGroupsMessage(filter));
        return channelGroups;
    }
    
    @Override
    public Future<List<ChannelGroup>> findChannelGroupsAsync(String filter) {
        initCheck();
        final Future<List<ChannelGroup>> futureChannelGroups = getMessageDispatcher().getRequestProcessor(FindChannelGroupsRequestProcessor.class).executeAsync(MessageBuilder.buildFindChannelGroupsMessage(filter));
        return futureChannelGroups;
    }

    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    public Transport getTransport() {
        return transport;
    }
    
    private MessageDispatcher getMessageDispatcher() {
        return messageDispatcher;
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    private void initCheck() {
        if (!isInitialized) {
            throw new IllegalStateException("!isInitialized");
        }
    }
}
