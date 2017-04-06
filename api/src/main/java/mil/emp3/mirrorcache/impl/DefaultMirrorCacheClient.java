package mil.emp3.mirrorcache.impl;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Future;

import org.cmapi.primitives.proto.CmapiProto.CreateChannelCommand;
import org.cmapi.primitives.proto.CmapiProto.CreateChannelGroupCommand;
import org.cmapi.primitives.proto.CmapiProto.DeleteChannelCommand;
import org.cmapi.primitives.proto.CmapiProto.DeleteChannelGroupCommand;
import org.cmapi.primitives.proto.CmapiProto.FindChannelGroupsCommand;
import org.cmapi.primitives.proto.CmapiProto.FindChannelsCommand;
import org.cmapi.primitives.proto.CmapiProto.GetClientInfoCommmand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand.CommandCase;

import mil.emp3.mirrorcache.Message;
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
        getMessageDispatcher().init();
        
        this.clientEventRegistration = on(ClientConnectEvent.TYPE, new ClientEventHandlerAdapter() {
            @Override public void onConnect(ClientConnectEvent event) {
                /*
                 * Retrieve 'clientInfo' from server.
                 */
                final Message reqMessage = new Message();
                reqMessage.setCommand(CommandCase.GET_CLIENT_INFO, GetClientInfoCommmand.newBuilder()
                                                                                        .build());

                try {
                    final ClientInfo clientInfo = getMessageDispatcher().getRequestProcessor(GetClientInfoRequestProcessor.class).executeSync(reqMessage);
                    DefaultMirrorCacheClient.this.clientId = clientInfo;

                } catch (MirrorCacheException e) {
                    throw new IllegalStateException("Unable to retrieve clientInfo: " + e.getMessage(), e);
                }
            }
        });
    }
    
    @Override
    public void shutdown() throws MirrorCacheException {
        /*
         * We are no longer interested in receiving ClientConnectEvent events.
         */
        if (clientEventRegistration != null) {
            clientEventRegistration.removeHandler();
            clientEventRegistration = null;
        }
        
        disconnect();
        getMessageDispatcher().shutdown();
    }
    
    @Override
    public void connect() throws MirrorCacheException {
        getTransport().connect();
    }

    @Override
    public void disconnect() throws MirrorCacheException {
        getTransport().disconnect();
    }
    
    @Override
    public <T extends EventHandler> EventRegistration on(MirrorCacheEvent.Type<T> type, T handler) {
        return getMessageDispatcher().on(type, handler);
    }

    @Override
    public ClientInfo getClientInfo() {
        return clientId;
    }
    
    @Override
    public Channel createChannel(String name, Channel.Visibility visibility, Channel.Type type) throws MirrorCacheException {
        final Message reqMessage = new Message();
        reqMessage.setCommand(OneOfCommand.newBuilder()
                                          .setCreateChannel(CreateChannelCommand.newBuilder()
                                                                                .setChannelName(name)
                                                                                .setType(type.name())
                                                                                .setVisibility(visibility.name()))
                                          .build());
        
        final Channel channel = getMessageDispatcher().getRequestProcessor(CreateChannelRequestProcessor.class).executeSync(reqMessage);
        return channel;
    }
    
    @Override
    public Future<Channel> createChannelAsync(final String name, final Channel.Visibility visibility, final Channel.Type type) {
        final Message reqMessage = new Message();
        reqMessage.setCommand(OneOfCommand.newBuilder()
                                          .setCreateChannel(CreateChannelCommand.newBuilder()
                                                                                .setChannelName(name)
                                                                                .setType(type.name())
                                                                                .setVisibility(visibility.name()))
                                          .build());
        
        final Future<Channel> futureChannel = getMessageDispatcher().getRequestProcessor(CreateChannelRequestProcessor.class).executeAsync(reqMessage);
        return futureChannel;
    }
    
    @Override
    public void deleteChannel(String name) throws MirrorCacheException {
        final Message reqMessage = new Message();
        reqMessage.setCommand(OneOfCommand.newBuilder()
                                          .setDeleteChannel(DeleteChannelCommand.newBuilder()
                                                                                .setChannelName(name))
                                          .build());
        
        getMessageDispatcher().getRequestProcessor(DeleteChannelRequestProcessor.class).executeSync(reqMessage);
    }

    @Override
    public Future<Void> deleteChannelAsync(String name) {
        final Message reqMessage = new Message();
        reqMessage.setCommand(OneOfCommand.newBuilder()
                                          .setDeleteChannel(DeleteChannelCommand.newBuilder()
                                                                                .setChannelName(name))
                                          .build());
        
        final Future<Void> futureVoid = getMessageDispatcher().getRequestProcessor(DeleteChannelRequestProcessor.class).executeAsync(reqMessage);
        return futureVoid;
    }

    @Override
    public List<Channel> findChannels(String filter) throws MirrorCacheException {
        final Message reqMessage = new Message();
        reqMessage.setCommand(OneOfCommand.newBuilder()
                                          .setFindChannels(FindChannelsCommand.newBuilder()
                                                                              .setFilter(filter))
                                          .build());
        
        final List<Channel> channels = getMessageDispatcher().getRequestProcessor(FindChannelsRequestProcessor.class).executeSync(reqMessage);
        return channels;
    }
    
    @Override
    public Future<List<Channel>> findChannelsAsync(String filter) {
        final Message reqMessage = new Message();
        reqMessage.setCommand(OneOfCommand.newBuilder()
                                          .setFindChannels(FindChannelsCommand.newBuilder()
                                                                              .setFilter(filter))
                                          .build());
        
        final Future<List<Channel>> futureChannels = getMessageDispatcher().getRequestProcessor(FindChannelsRequestProcessor.class).executeAsync(reqMessage);
        return futureChannels;
    }
    
    @Override
    public ChannelGroup createChannelGroup(String name) throws MirrorCacheException {
        final Message reqMessage = new Message();
        reqMessage.setCommand(OneOfCommand.newBuilder()
                                          .setCreateChannelGroup(CreateChannelGroupCommand.newBuilder()
                                                                                          .setChannelGroupName(name))
                                          .build());
        
        final ChannelGroup channelGroup = getMessageDispatcher().getRequestProcessor(CreateChannelGroupRequestProcessor.class).executeSync(reqMessage);
        return channelGroup;
    }
    
    @Override
    public Future<ChannelGroup> createChannelGroupAsync(String name) {
        final Message reqMessage = new Message();
        reqMessage.setCommand(OneOfCommand.newBuilder()
                                          .setCreateChannelGroup(CreateChannelGroupCommand.newBuilder()
                                                                                          .setChannelGroupName(name))
                                          .build());
        
        final Future<ChannelGroup> futureChannelGroup = getMessageDispatcher().getRequestProcessor(CreateChannelGroupRequestProcessor.class).executeAsync(reqMessage);
        return futureChannelGroup;
    }
    
    @Override
    public void deleteChannelGroup(String name) throws MirrorCacheException {
        final Message reqMessage = new Message();
        reqMessage.setCommand(OneOfCommand.newBuilder()
                                          .setDeleteChannelGroup(DeleteChannelGroupCommand.newBuilder()
                                                                                          .setChannelGroupName(name))
                                          .build());
        
        getMessageDispatcher().getRequestProcessor(DeleteChannelGroupRequestProcessor.class).executeSync(reqMessage);
    }
    
    @Override
    public Future<Void> deleteChannelGroupAsync(String name) {
        final Message reqMessage = new Message();
        reqMessage.setCommand(OneOfCommand.newBuilder()
                                          .setDeleteChannelGroup(DeleteChannelGroupCommand.newBuilder()
                                                                                          .setChannelGroupName(name))
                                          .build());
        
        final Future<Void> futureVoid = getMessageDispatcher().getRequestProcessor(DeleteChannelGroupRequestProcessor.class).executeAsync(reqMessage);
        return futureVoid;
    }
    
    @Override
    public List<ChannelGroup> findChannelGroups(String filter) throws MirrorCacheException {
        final Message reqMessage = new Message();
        reqMessage.setCommand(OneOfCommand.newBuilder()
                                          .setFindChannelGroups(FindChannelGroupsCommand.newBuilder()
                                                                                        .setFilter(filter))
                                          .build());
        
        final List<ChannelGroup> channelGroups = getMessageDispatcher().getRequestProcessor(FindChannelGroupsRequestProcessor.class).executeSync(reqMessage);
        return channelGroups;
    }
    
    @Override
    public Future<List<ChannelGroup>> findChannelGroupsAsync(String filter) {
        final Message reqMessage = new Message();
        reqMessage.setCommand(OneOfCommand.newBuilder()
                                          .setFindChannelGroups(FindChannelGroupsCommand.newBuilder()
                                                                                        .setFilter(filter))
                                          .build());
        
        final Future<List<ChannelGroup>> futureChannelGroups = getMessageDispatcher().getRequestProcessor(FindChannelGroupsRequestProcessor.class).executeAsync(reqMessage);
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
}
