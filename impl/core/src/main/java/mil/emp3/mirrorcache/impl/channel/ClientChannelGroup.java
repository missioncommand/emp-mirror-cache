package mil.emp3.mirrorcache.impl.channel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.Member;
import mil.emp3.mirrorcache.MessageDispatcher;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Payload;
import mil.emp3.mirrorcache.channel.ChannelGroup;
import mil.emp3.mirrorcache.channel.ChannelGroupCache;
import mil.emp3.mirrorcache.channel.ChannelGroupHistory;
import mil.emp3.mirrorcache.event.ChannelGroupEvent;
import mil.emp3.mirrorcache.event.EventHandler;
import mil.emp3.mirrorcache.event.EventRegistration;
import mil.emp3.mirrorcache.impl.MessageBuilder;
import mil.emp3.mirrorcache.impl.request.ChannelGroupAddChannelRequestProcessor;
import mil.emp3.mirrorcache.impl.request.ChannelGroupCacheRequestProcessor;
import mil.emp3.mirrorcache.impl.request.ChannelGroupCloseRequestProcessor;
import mil.emp3.mirrorcache.impl.request.ChannelGroupDeleteRequestProcessor;
import mil.emp3.mirrorcache.impl.request.ChannelGroupHistoryRequestProcessor;
import mil.emp3.mirrorcache.impl.request.ChannelGroupOpenRequestProcessor;
import mil.emp3.mirrorcache.impl.request.ChannelGroupPublishRequestProcessor;
import mil.emp3.mirrorcache.impl.request.ChannelGroupRemoveChannelRequestProcessor;

public class ClientChannelGroup implements ChannelGroup {
    static final private Logger LOG = LoggerFactory.getLogger(ClientChannelGroup.class);
    
    private boolean isOpen;
    
    final private Set<ClientChannel> channels;
    final private Set<Member> members;
    
    final private String name;
    final private MessageDispatcher dispatcher;
    
    public ClientChannelGroup(String name, boolean isOpen, MessageDispatcher dispatcher) {
        this(name, isOpen, Collections.<ClientChannel>emptySet(), Collections.<Member>emptySet(), dispatcher);
    }
    public ClientChannelGroup(String name, boolean isOpen, Set<ClientChannel> channels, Set<Member> members, MessageDispatcher dispatcher) {
        this.name       = name;
        this.isOpen     = isOpen;
        this.dispatcher = dispatcher;
        this.channels   = new HashSet<>(channels);
        this.members    = new HashSet<>(members);
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    public void setChannels(Set<ClientChannel> channels) {
        this.channels.clear();
        this.channels.addAll(channels);
    }
    public void setMembers(Set<Member> members) {
        this.members.clear();
        this.members.addAll(members);
    }
    
    public Set<ClientChannel> getChannels() {
        return Collections.unmodifiableSet(channels);
    }
    public Set<Member> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    @Override
    public <T extends EventHandler> EventRegistration on(ChannelGroupEvent.Type<T> eventType, T handler) {
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
    public void addChannel(String channelName) throws MirrorCacheException {
        LOG.debug("ChannelGroup[" + name + "].addChannel()");
        dispatcher.getRequestProcessor(ChannelGroupAddChannelRequestProcessor.class).executeSync(MessageBuilder.buildChannelGroupAddChannelMessage(name, channelName));
    }
    
    @Override
    public void removeChannel(String channelName) throws MirrorCacheException {
        LOG.debug("ChannelGroup[" + name + "].removeChannel()");
        dispatcher.getRequestProcessor(ChannelGroupRemoveChannelRequestProcessor.class).executeSync(MessageBuilder.buildChannelGroupRemoveChannelMessage(name, channelName));
    }

    @Override
    public void open() throws MirrorCacheException {
        LOG.debug("ChannelGroup[" + name + "].open()");
        dispatcher.getRequestProcessor(ChannelGroupOpenRequestProcessor.class).executeSync(MessageBuilder.buildChannelGroupOpenMessage(name));
        isOpen = true;
    }
    
    @Override
    public void close() throws MirrorCacheException {
        LOG.debug("ChannelGroup[" + name + "].close()");
        dispatcher.getRequestProcessor(ChannelGroupCloseRequestProcessor.class).executeSync(MessageBuilder.buildChannelGroupCloseMessage(name));
        isOpen = false;
    }
    
    @Override
    public void publish(String id, Class<?> type, Object payload) throws MirrorCacheException {
        LOG.debug("ChannelGroup[" + name + "].publish()");

        isOpenCheck();
        dispatcher.getRequestProcessor(ChannelGroupPublishRequestProcessor.class).executeSync(MessageBuilder.buildChannelGroupPublishMessage(name, new Payload<>(id, type.getName(), payload)));
    }
    
    @Override
    public void delete(String id) throws MirrorCacheException {
        LOG.debug("ChannelGroup[" + name + "].delete()");
        
        isOpenCheck();
        dispatcher.getRequestProcessor(ChannelGroupDeleteRequestProcessor.class).executeSync(MessageBuilder.buildChannelGroupDeleteMessage(name, id));
    }
    
    @Override
    public ChannelGroupCache cache() throws MirrorCacheException {
      LOG.debug("ChannelGroup[" + name + "].cache()");
      
      isOpenCheck();
      final ChannelGroupCache channelGroupCache = dispatcher.getRequestProcessor(ChannelGroupCacheRequestProcessor.class).executeSync(MessageBuilder.buildChannelGroupCacheMessage(name));
      return channelGroupCache;
    }
    
    @Override
    public ChannelGroupHistory history() throws MirrorCacheException {
        LOG.debug("ChannelGroup[" + name + "].history()");
        
        isOpenCheck();
        final ChannelGroupHistory channelGroupHistory = dispatcher.getRequestProcessor(ChannelGroupHistoryRequestProcessor.class).executeSync(MessageBuilder.buildChannelGroupHistoryMessage(name));
        return channelGroupHistory;
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    private void isOpenCheck() throws MirrorCacheException {
        if (!isOpen()) {
            throw new MirrorCacheException(Reason.CHANNELGROUP_NOT_OPEN);
        }
    }
    
    @Override
    public String toString() {
        return "ClientChannelGroup [name=" + name + ", channels=" + channels + ", members=" + members + "]";
    }
    
}
