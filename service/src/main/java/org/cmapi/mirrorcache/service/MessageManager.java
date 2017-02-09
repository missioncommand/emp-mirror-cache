package org.cmapi.mirrorcache.service;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.cmapi.mirrorcache.service.event.MessageEvent;
import org.cmapi.mirrorcache.service.processor.ChannelCacheProcessor;
import org.cmapi.mirrorcache.service.processor.ChannelCloseProcessor;
import org.cmapi.mirrorcache.service.processor.ChannelGroupAddChannelProcessor;
import org.cmapi.mirrorcache.service.processor.ChannelGroupCacheProcessor;
import org.cmapi.mirrorcache.service.processor.ChannelGroupHistoryProcessor;
import org.cmapi.mirrorcache.service.processor.ChannelGroupJoinProcessor;
import org.cmapi.mirrorcache.service.processor.ChannelGroupLeaveProcessor;
import org.cmapi.mirrorcache.service.processor.ChannelGroupPublishProcessor;
import org.cmapi.mirrorcache.service.processor.ChannelGroupRemoveChannelProcessor;
import org.cmapi.mirrorcache.service.processor.ChannelHistoryProcessor;
import org.cmapi.mirrorcache.service.processor.ChannelOpenProcessor;
import org.cmapi.mirrorcache.service.processor.ChannelPublishProcessor;
import org.cmapi.mirrorcache.service.processor.CommandProcessor;
import org.cmapi.mirrorcache.service.processor.CreateChannelGroupProcessor;
import org.cmapi.mirrorcache.service.processor.CreateChannelProcessor;
import org.cmapi.mirrorcache.service.processor.DeleteChannelGroupProcessor;
import org.cmapi.mirrorcache.service.processor.DeleteChannelProcessor;
import org.cmapi.mirrorcache.service.processor.FindChannelGroupsProcessor;
import org.cmapi.mirrorcache.service.processor.FindChannelsProcesor;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand.CommandCase;
import org.cmapi.primitives.proto.CmapiProto.ProtoMessage;
import org.slf4j.Logger;

@ApplicationScoped
public class MessageManager {
    
    @Inject
    Logger LOG;
    
    @Inject private CreateChannelProcessor createChannelProcessor;
    @Inject private DeleteChannelProcessor deleteChannelProcessor;
    @Inject private FindChannelsProcesor findChannelsProcessor;
    @Inject private ChannelOpenProcessor channelOpenProcessor;
    @Inject private ChannelCloseProcessor channelCloseProcessor;
    @Inject private ChannelPublishProcessor channelPublishProcessor;
    @Inject private ChannelCacheProcessor channelCacheProcessor;
    @Inject private ChannelHistoryProcessor channelHistoryProcessor;
    
    @Inject private CreateChannelGroupProcessor createChannelGroupProcessor;
    @Inject private DeleteChannelGroupProcessor deleteChannelGroupProcessor;
    @Inject private FindChannelGroupsProcessor findChannelGroupsProcessor;
    @Inject private ChannelGroupJoinProcessor channelGroupJoinProcessor;
    @Inject private ChannelGroupLeaveProcessor channelGroupLeaveProcessor;
    @Inject private ChannelGroupAddChannelProcessor channelGroupAddChannelProcessor;
    @Inject private ChannelGroupRemoveChannelProcessor channelGroupRemoveChannelProcessor;
    @Inject private ChannelGroupPublishProcessor channelGroupPublishProcessor;
    @Inject private ChannelGroupCacheProcessor channelGroupCacheProcessor;
    @Inject private ChannelGroupHistoryProcessor channelGroupHistoryProcessor;
    
    private Map<CommandCase, CommandProcessor> commandProcessors;
    
    @PostConstruct
    public void init() {
        this.commandProcessors = new EnumMap<>(CommandCase.class);
        this.commandProcessors.put(CommandCase.CREATE_CHANNEL , createChannelProcessor);
        this.commandProcessors.put(CommandCase.DELETE_CHANNEL , deleteChannelProcessor);
        this.commandProcessors.put(CommandCase.FIND_CHANNELS  , findChannelsProcessor);
        this.commandProcessors.put(CommandCase.CHANNEL_OPEN   , channelOpenProcessor);
        this.commandProcessors.put(CommandCase.CHANNEL_CLOSE  , channelCloseProcessor);
        this.commandProcessors.put(CommandCase.CHANNEL_PUBLISH, channelPublishProcessor);
        this.commandProcessors.put(CommandCase.CHANNEL_CACHE  , channelCacheProcessor);
        this.commandProcessors.put(CommandCase.CHANNEL_HISTORY, channelHistoryProcessor);
        
        this.commandProcessors.put(CommandCase.CREATE_CHANNEL_GROUP        , createChannelGroupProcessor);
        this.commandProcessors.put(CommandCase.DELETE_CHANNEL_GROUP        , deleteChannelGroupProcessor);
        this.commandProcessors.put(CommandCase.FIND_CHANNEL_GROUPS         , findChannelGroupsProcessor);
        this.commandProcessors.put(CommandCase.CHANNEL_GROUP_JOIN          , channelGroupJoinProcessor);
        this.commandProcessors.put(CommandCase.CHANNEL_GROUP_LEAVE         , channelGroupLeaveProcessor);
        this.commandProcessors.put(CommandCase.CHANNEL_GROUP_ADD_CHANNEL   , channelGroupAddChannelProcessor);
        this.commandProcessors.put(CommandCase.CHANNEL_GROUP_REMOVE_CHANNEL, channelGroupRemoveChannelProcessor);
        this.commandProcessors.put(CommandCase.CHANNEL_GROUP_PUBLISH       , channelGroupPublishProcessor);
        this.commandProcessors.put(CommandCase.CHANNEL_GROUP_CACHE         , channelGroupCacheProcessor);
        this.commandProcessors.put(CommandCase.CHANNEL_GROUP_HISTORY       , channelGroupHistoryProcessor);
    }
    
    public void onMessage(@Observes MessageEvent event) {
        final ProtoMessage message = event.getMessage();
        
        final CommandCase command = message.getCommand().getCommandCase();
        
        final CommandProcessor processor = commandProcessors.get(command);
        if (processor != null) {
            processor.process(event.getSessionId(), message);
            
        } else {
            throw new IllegalStateException("Unable to locate processor for: " + command);
        }
    }
}
