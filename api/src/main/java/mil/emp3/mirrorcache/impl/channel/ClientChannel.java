package mil.emp3.mirrorcache.impl.channel;

import org.cmapi.primitives.proto.CmapiProto.ChannelCacheCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelCloseCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelHistoryCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelOpenCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelPublishCommand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand.CommandCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Payload;
import mil.emp3.mirrorcache.channel.Channel;
import mil.emp3.mirrorcache.channel.ChannelCache;
import mil.emp3.mirrorcache.channel.ChannelHistory;
import mil.emp3.mirrorcache.event.ChannelEvent;
import mil.emp3.mirrorcache.event.EventHandler;
import mil.emp3.mirrorcache.event.EventRegistration;
import mil.emp3.mirrorcache.impl.MessageDispatcher;
import mil.emp3.mirrorcache.impl.request.ChannelCacheRequestProcessor;
import mil.emp3.mirrorcache.impl.request.ChannelCloseRequestProcessor;
import mil.emp3.mirrorcache.impl.request.ChannelHistoryRequestProcessor;
import mil.emp3.mirrorcache.impl.request.ChannelOpenRequestProcessor;
import mil.emp3.mirrorcache.impl.request.ChannelPublishRequestProcessor;

public class ClientChannel implements Channel {
    static final private Logger LOG = LoggerFactory.getLogger(ClientChannel.class);
    
    private boolean isOpen;
    
    final private String name;
    final private Visibility visibility;
    final private Type type;
    
    final private MessageDispatcher dispatcher;
    
    public ClientChannel(String name, Visibility visibility, Type type, boolean isOpen, MessageDispatcher dispatcher) {
        this.name       = name;
        this.visibility = visibility;
        this.type       = type;
        this.isOpen     = isOpen;
        
        this.dispatcher = dispatcher;
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    @Override
    public <T extends EventHandler> EventRegistration on(ChannelEvent.Type<T> eventType, T handler) {
        return dispatcher.on(eventType, handler);
    }
    
    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Visibility getVisibility() {
        return visibility;
    }
    
    @Override
    public ChannelCache cache() throws MirrorCacheException {
      LOG.debug("Channel[" + name + "].cache()");
      
      if (!isOpen()) {
          throw new MirrorCacheException(Reason.CHANNEL_NOT_OPEN);
      }
      
      final Message reqMessage = new Message();
      reqMessage.setCommand(CommandCase.CHANNEL_CACHE, ChannelCacheCommand.newBuilder()
                                                                          .setChannelName(name)
                                                                          .build());

      final ChannelCache channelCache = dispatcher.getRequestProcessor(ChannelCacheRequestProcessor.class).executeSync(reqMessage);
      return channelCache;
    }
    
    @Override
    public ChannelHistory history() throws MirrorCacheException {
        LOG.debug("Channel[" + name + "].history()");
        
        if (!isOpen()) {
            throw new MirrorCacheException(Reason.CHANNEL_NOT_OPEN);
        }
        
        final Message reqMessage = new Message();
        reqMessage.setCommand(CommandCase.CHANNEL_HISTORY, ChannelHistoryCommand.newBuilder()
                                                                                .setChannelName(name)
                                                                                .build());

        final ChannelHistory channelHistory = dispatcher.getRequestProcessor(ChannelHistoryRequestProcessor.class).executeSync(reqMessage);
        return channelHistory;
    }
    
    @Override
    public void publish(String id, Object payload) throws MirrorCacheException {
        LOG.debug("Channel[" + name + "].publish()");
        
        if (!isOpen()) {
            throw new MirrorCacheException(Reason.CHANNEL_NOT_OPEN);
        }
        
        final Message reqMessage = new Message();
        reqMessage.setPayload(new Payload<>(id, payload.getClass().getName(), payload));
        reqMessage.setCommand(CommandCase.CHANNEL_PUBLISH, ChannelPublishCommand.newBuilder()
                                                                                .setChannelName(name)
                                                                                .build());
        
        dispatcher.getRequestProcessor(ChannelPublishRequestProcessor.class).executeSync(reqMessage);
    }
    
    @Override
    public void open(Flow flow, String filter) throws MirrorCacheException {
        LOG.debug("Channel[" + name + "].open()");

        final Message reqMessage = new Message();
        reqMessage.setCommand(CommandCase.CHANNEL_OPEN, ChannelOpenCommand.newBuilder()
                                                                          .setChannelName(name)
                                                                          .setFlow(flow.name())
                                                                          .setFilter(filter)
                                                                          .build());
        
        dispatcher.getRequestProcessor(ChannelOpenRequestProcessor.class).executeSync(reqMessage);
        
        isOpen = true;
    }
    
    @Override
    public void close() throws MirrorCacheException {
        LOG.debug("Channel[" + name + "].close()");
        
        final Message reqMessage = new Message();
        reqMessage.setCommand(CommandCase.CHANNEL_CLOSE, ChannelCloseCommand.newBuilder()
                                                                            .setChannelName(name)
                                                                            .build());
        
        dispatcher.getRequestProcessor(ChannelCloseRequestProcessor.class).executeSync(reqMessage);
        
        isOpen = false;
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //

    @Override
    public String toString() {
        return "ClientChannel [name=" + name + ", visibility=" + visibility + ", type=" + type + "]";
    }

}
