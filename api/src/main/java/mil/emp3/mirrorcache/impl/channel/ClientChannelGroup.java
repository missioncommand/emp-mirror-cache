package mil.emp3.mirrorcache.impl.channel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.cmapi.primitives.proto.CmapiProto.ChannelGroupAddChannelCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupCacheCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupHistoryCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupJoinCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupLeaveCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupPublishCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupRemoveChannelCommand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand.CommandCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.Member;
import mil.emp3.mirrorcache.Message;
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
import mil.emp3.mirrorcache.impl.request.ChannelGroupAddChannelRequestProcessor;
import mil.emp3.mirrorcache.impl.request.ChannelGroupCacheRequestProcessor;
import mil.emp3.mirrorcache.impl.request.ChannelGroupHistoryRequestProcessor;
import mil.emp3.mirrorcache.impl.request.ChannelGroupJoinRequestProcessor;
import mil.emp3.mirrorcache.impl.request.ChannelGroupLeaveRequestProcessor;
import mil.emp3.mirrorcache.impl.request.ChannelGroupPublishRequestProcessor;
import mil.emp3.mirrorcache.impl.request.ChannelGroupRemoveChannelRequestProcessor;

public class ClientChannelGroup implements ChannelGroup {
    static final private Logger LOG = LoggerFactory.getLogger(ClientChannelGroup.class);
    
    private boolean isJoined;
    
    final private Set<ClientChannel> channels;
    final private Set<Member> members;
    
    final private String name;
    final private MessageDispatcher dispatcher;
    
    public ClientChannelGroup(String name, boolean isJoined, MessageDispatcher dispatcher) {
        this(name, isJoined, Collections.<ClientChannel>emptySet(), Collections.<Member>emptySet(), dispatcher);
    }
    public ClientChannelGroup(String name, boolean isJoined, Set<ClientChannel> channels, Set<Member> members, MessageDispatcher dispatcher) {
        this.name       = name;
        this.isJoined   = isJoined;
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
    public boolean isJoined() {
        return isJoined;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void addChannel(String channelName) throws MirrorCacheException {
        LOG.debug("ChannelGroup[" + name + "].addChannel()");

//        if (!isJoined) {
//            throw new MirrorCacheException(Reason.CHANNELGROUP_NOT_JOINED);
//        }
        
        final Message reqMessage = new Message();
        reqMessage.setCommand(CommandCase.CHANNEL_GROUP_ADD_CHANNEL, ChannelGroupAddChannelCommand.newBuilder()
                                                                                                  .setChannelGroupName(name)
                                                                                                  .setChannelName(channelName)
                                                                                                  .build());

        dispatcher.getRequestProcessor(ChannelGroupAddChannelRequestProcessor.class).executeSync(reqMessage);
    }

    @Override
    public void removeChannel(String channelName) throws MirrorCacheException {
        LOG.debug("ChannelGroup[" + name + "].removeChannel()");

//        if (!isJoined) {
//            throw new MirrorCacheException(Reason.CHANNELGROUP_NOT_JOINED);
//        }
        
        final Message reqMessage = new Message();
        reqMessage.setCommand(CommandCase.CHANNEL_GROUP_REMOVE_CHANNEL, ChannelGroupRemoveChannelCommand.newBuilder()
                                                                                                        .setChannelGroupName(name)
                                                                                                        .setChannelName(channelName)
                                                                                                        .build());

        dispatcher.getRequestProcessor(ChannelGroupRemoveChannelRequestProcessor.class).executeSync(reqMessage);
    }

    @Override
    public void join() throws MirrorCacheException {
        LOG.debug("ChannelGroup[" + name + "].join()");

        final Message reqMessage = new Message();
        reqMessage.setCommand(CommandCase.CHANNEL_GROUP_JOIN, ChannelGroupJoinCommand.newBuilder()
                                                                                     .setChannelGroupName(name)
                                                                                     .build());

        dispatcher.getRequestProcessor(ChannelGroupJoinRequestProcessor.class).executeSync(reqMessage);
        
        isJoined = true;
    }

    @Override
    public void leave() throws MirrorCacheException {
        LOG.debug("ChannelGroup[" + name + "].leave()");

        final Message reqMessage = new Message();
        reqMessage.setCommand(CommandCase.CHANNEL_GROUP_LEAVE, ChannelGroupLeaveCommand.newBuilder()
                                                                                       .setChannelGroupName(name)
                                                                                       .build());

        dispatcher.getRequestProcessor(ChannelGroupLeaveRequestProcessor.class).executeSync(reqMessage);
        
        isJoined = false;
    }

    @Override
    public void publish(String id, Class<?> type, Object payload) throws MirrorCacheException {
        LOG.debug("ChannelGroup[" + name + "].publish()");

        if (!isJoined()) {
            throw new MirrorCacheException(Reason.CHANNELGROUP_NOT_JOINED);
        }
        
        final Message reqMessage = new Message();
        reqMessage.setPayload(new Payload<>(id, type.getName(), payload));
        reqMessage.setCommand(CommandCase.CHANNEL_GROUP_PUBLISH, ChannelGroupPublishCommand.newBuilder()
                                                                                           .setChannelGroupName(name)
                                                                                           .build());
        
        dispatcher.getRequestProcessor(ChannelGroupPublishRequestProcessor.class).executeSync(reqMessage);
    }
    
    @Override
    public ChannelGroupCache cache() throws MirrorCacheException {
      LOG.debug("ChannelGroup[" + name + "].cache()");
      
      if (!isJoined()) {
          throw new MirrorCacheException(Reason.CHANNELGROUP_NOT_JOINED);
      }
      
      final Message reqMessage = new Message();
      reqMessage.setCommand(CommandCase.CHANNEL_GROUP_CACHE, ChannelGroupCacheCommand.newBuilder()
                                                                                     .setChannelGroupName(name)
                                                                                     .build());

      final ChannelGroupCache channelGroupCache = dispatcher.getRequestProcessor(ChannelGroupCacheRequestProcessor.class).executeSync(reqMessage);
      return channelGroupCache;
    }
    
    @Override
    public ChannelGroupHistory history() throws MirrorCacheException {
        LOG.debug("ChannelGroup[" + name + "].history()");
        
        if (!isJoined()) {
            throw new MirrorCacheException(Reason.CHANNELGROUP_NOT_JOINED);
        }
        
        final Message reqMessage = new Message();
        reqMessage.setCommand(CommandCase.CHANNEL_GROUP_HISTORY, ChannelGroupHistoryCommand.newBuilder()
                                                                                     .setChannelGroupName(name)
                                                                                     .build());

        final ChannelGroupHistory channelGroupHistory = dispatcher.getRequestProcessor(ChannelGroupHistoryRequestProcessor.class).executeSync(reqMessage);
        return channelGroupHistory;
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    @Override
    public String toString() {
        return "ClientChannelGroup [name=" + name + ", channels=" + channels + ", members=" + members + "]";
    }
    
}
