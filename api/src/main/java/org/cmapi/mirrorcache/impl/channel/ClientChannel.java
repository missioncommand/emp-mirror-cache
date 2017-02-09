package org.cmapi.mirrorcache.impl.channel;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.MirrorCacheException.Reason;
import org.cmapi.mirrorcache.channel.Channel;
import org.cmapi.mirrorcache.channel.ChannelCache;
import org.cmapi.mirrorcache.channel.ChannelHistory;
import org.cmapi.mirrorcache.Payload;
import org.cmapi.mirrorcache.event.ChannelEvent;
import org.cmapi.mirrorcache.event.EventHandler;
import org.cmapi.mirrorcache.event.EventRegistration;
import org.cmapi.mirrorcache.impl.MessageDispatcher;
import org.cmapi.mirrorcache.impl.request.ChannelCacheRequestProcessor;
import org.cmapi.mirrorcache.impl.request.ChannelCloseRequestProcessor;
import org.cmapi.mirrorcache.impl.request.ChannelHistoryRequestProcessor;
import org.cmapi.mirrorcache.impl.request.ChannelOpenRequestProcessor;
import org.cmapi.mirrorcache.impl.request.ChannelPublishRequestProcessor;
import org.cmapi.primitives.proto.CmapiProto.ChannelCacheCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelCloseCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelHistoryCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelOpenCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelPublishCommand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand.CommandCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
