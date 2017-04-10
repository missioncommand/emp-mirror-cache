package mil.emp3.mirrorcache.service;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.cmapi.primitives.proto.CmapiProto.OneOfOperation.OperationCase;
import org.cmapi.primitives.proto.CmapiProto.ProtoMessage;

import mil.emp3.mirrorcache.service.event.MessageEvent;
import mil.emp3.mirrorcache.service.processor.ChannelCacheProcessor;
import mil.emp3.mirrorcache.service.processor.ChannelCloseProcessor;
import mil.emp3.mirrorcache.service.processor.ChannelDeleteProcessor;
import mil.emp3.mirrorcache.service.processor.ChannelGroupAddChannelProcessor;
import mil.emp3.mirrorcache.service.processor.ChannelGroupCacheProcessor;
import mil.emp3.mirrorcache.service.processor.ChannelGroupCloseProcessor;
import mil.emp3.mirrorcache.service.processor.ChannelGroupDeleteProcessor;
import mil.emp3.mirrorcache.service.processor.ChannelGroupHistoryProcessor;
import mil.emp3.mirrorcache.service.processor.ChannelGroupOpenProcessor;
import mil.emp3.mirrorcache.service.processor.ChannelGroupPublishProcessor;
import mil.emp3.mirrorcache.service.processor.ChannelGroupRemoveChannelProcessor;
import mil.emp3.mirrorcache.service.processor.ChannelHistoryProcessor;
import mil.emp3.mirrorcache.service.processor.ChannelOpenProcessor;
import mil.emp3.mirrorcache.service.processor.ChannelPublishProcessor;
import mil.emp3.mirrorcache.service.processor.OperationProcessor;
import mil.emp3.mirrorcache.service.processor.CreateChannelGroupProcessor;
import mil.emp3.mirrorcache.service.processor.CreateChannelProcessor;
import mil.emp3.mirrorcache.service.processor.DeleteChannelGroupProcessor;
import mil.emp3.mirrorcache.service.processor.DeleteChannelProcessor;
import mil.emp3.mirrorcache.service.processor.FindChannelGroupsProcessor;
import mil.emp3.mirrorcache.service.processor.FindChannelsProcesor;
import mil.emp3.mirrorcache.service.processor.GetClientInfoProcessor;

@ApplicationScoped
public class MessageManager {
    
    @Inject private GetClientInfoProcessor getClientInfoProcessor;

    @Inject private CreateChannelProcessor createChannelProcessor;
    @Inject private DeleteChannelProcessor deleteChannelProcessor;
    @Inject private FindChannelsProcesor findChannelsProcessor;
    @Inject private ChannelOpenProcessor channelOpenProcessor;
    @Inject private ChannelCloseProcessor channelCloseProcessor;
    @Inject private ChannelPublishProcessor channelPublishProcessor;
    @Inject private ChannelDeleteProcessor channelDeleteProcessor;
    @Inject private ChannelCacheProcessor channelCacheProcessor;
    @Inject private ChannelHistoryProcessor channelHistoryProcessor;
    
    @Inject private CreateChannelGroupProcessor createChannelGroupProcessor;
    @Inject private DeleteChannelGroupProcessor deleteChannelGroupProcessor;
    @Inject private FindChannelGroupsProcessor findChannelGroupsProcessor;
    @Inject private ChannelGroupOpenProcessor channelGroupOpenProcessor;
    @Inject private ChannelGroupCloseProcessor channelGroupCloseProcessor;
    @Inject private ChannelGroupAddChannelProcessor channelGroupAddChannelProcessor;
    @Inject private ChannelGroupRemoveChannelProcessor channelGroupRemoveChannelProcessor;
    @Inject private ChannelGroupPublishProcessor channelGroupPublishProcessor;
    @Inject private ChannelGroupDeleteProcessor channelGroupDeleteProcessor;
    @Inject private ChannelGroupCacheProcessor channelGroupCacheProcessor;
    @Inject private ChannelGroupHistoryProcessor channelGroupHistoryProcessor;
    
    private Map<OperationCase, OperationProcessor> operationProcessors;
    
    @PostConstruct
    public void init() {
        this.operationProcessors = new EnumMap<>(OperationCase.class);
        this.operationProcessors.put(OperationCase.GET_CLIENT_INFO, getClientInfoProcessor);
        
        this.operationProcessors.put(OperationCase.CREATE_CHANNEL , createChannelProcessor);
        this.operationProcessors.put(OperationCase.DELETE_CHANNEL , deleteChannelProcessor);
        this.operationProcessors.put(OperationCase.FIND_CHANNELS  , findChannelsProcessor);
        this.operationProcessors.put(OperationCase.CHANNEL_OPEN   , channelOpenProcessor);
        this.operationProcessors.put(OperationCase.CHANNEL_CLOSE  , channelCloseProcessor);
        this.operationProcessors.put(OperationCase.CHANNEL_PUBLISH, channelPublishProcessor);
        this.operationProcessors.put(OperationCase.CHANNEL_DELETE , channelDeleteProcessor);
        this.operationProcessors.put(OperationCase.CHANNEL_CACHE  , channelCacheProcessor);
        this.operationProcessors.put(OperationCase.CHANNEL_HISTORY, channelHistoryProcessor);
        
        this.operationProcessors.put(OperationCase.CREATE_CHANNEL_GROUP        , createChannelGroupProcessor);
        this.operationProcessors.put(OperationCase.DELETE_CHANNEL_GROUP        , deleteChannelGroupProcessor);
        this.operationProcessors.put(OperationCase.FIND_CHANNEL_GROUPS         , findChannelGroupsProcessor);
        this.operationProcessors.put(OperationCase.CHANNEL_GROUP_OPEN          , channelGroupOpenProcessor);
        this.operationProcessors.put(OperationCase.CHANNEL_GROUP_CLOSE         , channelGroupCloseProcessor);
        this.operationProcessors.put(OperationCase.CHANNEL_GROUP_ADD_CHANNEL   , channelGroupAddChannelProcessor);
        this.operationProcessors.put(OperationCase.CHANNEL_GROUP_REMOVE_CHANNEL, channelGroupRemoveChannelProcessor);
        this.operationProcessors.put(OperationCase.CHANNEL_GROUP_PUBLISH       , channelGroupPublishProcessor);
        this.operationProcessors.put(OperationCase.CHANNEL_GROUP_DELETE        , channelGroupDeleteProcessor);
        this.operationProcessors.put(OperationCase.CHANNEL_GROUP_CACHE         , channelGroupCacheProcessor);
        this.operationProcessors.put(OperationCase.CHANNEL_GROUP_HISTORY       , channelGroupHistoryProcessor);
    }
    
    public void onMessage(@Observes MessageEvent event) {
        final ProtoMessage message = event.getMessage();
        
        final OperationCase operation = message.getOperation().getOperationCase();
        
        final OperationProcessor processor = operationProcessors.get(operation);
        if (processor != null) {
            processor.process(event.getSessionId(), message);
            
        } else {
            throw new IllegalStateException("Unable to locate processor for: " + operation);
        }
    }
}
