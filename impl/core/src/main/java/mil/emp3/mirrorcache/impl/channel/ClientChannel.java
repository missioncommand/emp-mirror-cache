package mil.emp3.mirrorcache.impl.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.MessageDispatcher;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Payload;
import mil.emp3.mirrorcache.channel.Channel;
import mil.emp3.mirrorcache.channel.ChannelCache;
import mil.emp3.mirrorcache.channel.ChannelHistory;
import mil.emp3.mirrorcache.event.ChannelEvent;
import mil.emp3.mirrorcache.event.EventHandler;
import mil.emp3.mirrorcache.event.EventRegistration;
import mil.emp3.mirrorcache.impl.MessageBuilder;
import mil.emp3.mirrorcache.impl.request.ChannelCacheRequestProcessor;
import mil.emp3.mirrorcache.impl.request.ChannelCloseRequestProcessor;
import mil.emp3.mirrorcache.impl.request.ChannelDeleteRequestProcessor;
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

      isOpenCheck();
      final ChannelCache channelCache = dispatcher.getRequestProcessor(ChannelCacheRequestProcessor.class).executeSync(MessageBuilder.buildChannelCacheMessage(name));
      return channelCache;
    }
    
    @Override
    public ChannelHistory history() throws MirrorCacheException {
        LOG.debug("Channel[" + name + "].history()");

        isOpenCheck();
        final ChannelHistory channelHistory = dispatcher.getRequestProcessor(ChannelHistoryRequestProcessor.class).executeSync(MessageBuilder.buildChannelHistoryMessage(name));
        return channelHistory;
    }
    
    @Override
    public void publish(String id, Class<?> type, Object payload) throws MirrorCacheException {
        LOG.debug("Channel[" + name + "].publish()");
        
        isOpenCheck();
        dispatcher.getRequestProcessor(ChannelPublishRequestProcessor.class).executeSync(MessageBuilder.buildChannelPublishMessage(name, new Payload<>(id, type.getName(), payload)));
    }
    
    @Override
    public void delete(String id) throws MirrorCacheException {
        LOG.debug("Channel[" + name + "].delete()");
        
        isOpenCheck();
        dispatcher.getRequestProcessor(ChannelDeleteRequestProcessor.class).executeSync(MessageBuilder.buildChannelDeleteMessage(name, id));
    }
    
    @Override
    public void open(Flow flow, String filter) throws MirrorCacheException {
        LOG.debug("Channel[" + name + "].open()");

        dispatcher.getRequestProcessor(ChannelOpenRequestProcessor.class).executeSync(MessageBuilder.buildChannelOpenMessage(name, flow, filter));
        isOpen = true;
    }
    
    @Override
    public void close() throws MirrorCacheException {
        LOG.debug("Channel[" + name + "].close()");
        
        dispatcher.getRequestProcessor(ChannelCloseRequestProcessor.class).executeSync(MessageBuilder.buildChannelCloseMessage(name));
        isOpen = false;
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //

    private void isOpenCheck() throws MirrorCacheException {
        if (!isOpen()) {
            throw new MirrorCacheException(Reason.CHANNEL_NOT_OPEN);
        }
    }
    
    @Override
    public String toString() {
        return "ClientChannel [name=" + name + ", visibility=" + visibility + ", type=" + type + "]";
    }

}
