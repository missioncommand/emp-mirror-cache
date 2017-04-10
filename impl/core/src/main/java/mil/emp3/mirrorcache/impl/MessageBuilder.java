package mil.emp3.mirrorcache.impl;

import org.cmapi.primitives.proto.CmapiProto.ChannelCacheOperation;
import org.cmapi.primitives.proto.CmapiProto.ChannelCloseOperation;
import org.cmapi.primitives.proto.CmapiProto.ChannelDeleteOperation;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupAddChannelOperation;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupCacheOperation;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupCloseOperation;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupDeleteOperation;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupHistoryOperation;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupOpenOperation;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupPublishOperation;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupRemoveChannelOperation;
import org.cmapi.primitives.proto.CmapiProto.ChannelHistoryOperation;
import org.cmapi.primitives.proto.CmapiProto.ChannelOpenOperation;
import org.cmapi.primitives.proto.CmapiProto.ChannelPublishOperation;
import org.cmapi.primitives.proto.CmapiProto.CreateChannelOperation;
import org.cmapi.primitives.proto.CmapiProto.CreateChannelGroupOperation;
import org.cmapi.primitives.proto.CmapiProto.DeleteChannelOperation;
import org.cmapi.primitives.proto.CmapiProto.DeleteChannelGroupOperation;
import org.cmapi.primitives.proto.CmapiProto.FindChannelGroupsOperation;
import org.cmapi.primitives.proto.CmapiProto.FindChannelsOperation;
import org.cmapi.primitives.proto.CmapiProto.GetClientInfoCommmand;
import org.cmapi.primitives.proto.CmapiProto.OneOfOperation;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.Payload;
import mil.emp3.mirrorcache.channel.Channel;
import mil.emp3.mirrorcache.channel.Channel.Flow;

public class MessageBuilder {
    private MessageBuilder() { }
    
    static public Message buildFindChannelGroupsMessage(String filter) {
        final FindChannelGroupsOperation op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = FindChannelGroupsOperation.newBuilder()
                                                                                                .setFilter(filter)
                                                                                                .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setFindChannelGroups(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildDeleteChannelGroupMessage(String name) {
        final DeleteChannelGroupOperation op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = DeleteChannelGroupOperation.newBuilder()
                                                                                                 .setChannelGroupName(name)
                                                                                                 .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setDeleteChannelGroup(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildCreateChannelGroupMessage(String name) {
        final CreateChannelGroupOperation op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = CreateChannelGroupOperation.newBuilder()
                                                                                                 .setChannelGroupName(name)
                                                                                                 .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setCreateChannelGroup(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildFindChannelsMessage(String filter) {
        final FindChannelsOperation op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = FindChannelsOperation.newBuilder()
                                                                                           .setFilter(filter)
                                                                                           .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setFindChannels(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildDeleteChannelMessage(String name) {
        final DeleteChannelOperation op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = DeleteChannelOperation.newBuilder()
                                                                                            .setChannelName(name)
                                                                                            .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setDeleteChannel(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildCreateChannelMessage(String name, Channel.Visibility visibility, Channel.Type type) {
        final CreateChannelOperation op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = CreateChannelOperation.newBuilder()
                                                                                            .setChannelName(name)
                                                                                            .setType(type.name())
                                                                                            .setVisibility(visibility.name())
                                                                                            .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setCreateChannel(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildGetClientInfoMessage() {
        final GetClientInfoCommmand op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = GetClientInfoCommmand.newBuilder()
                                                                                           .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setGetClientInfo(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    static public Message buildChannelCloseMessage(String name) {
        final ChannelCloseOperation op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelCloseOperation.newBuilder()
                                                                                           .setChannelName(name)
                                                                                           .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setChannelClose(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelOpenMessage(String name, Flow flow, String filter) {
        final ChannelOpenOperation op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelOpenOperation.newBuilder()
                                                                                          .setChannelName(name)
                                                                                          .setFlow(flow.name())
                                                                                          .setFilter(filter)
                                                                                          .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setChannelOpen(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelDeleteMessage(String name, String payloadId) {
        final ChannelDeleteOperation op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelDeleteOperation.newBuilder()
                                                                                            .setChannelName(name)
                                                                                            .setPayloadId(payloadId)
                                                                                            .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setChannelDelete(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelPublishMessage(String name, Payload<?> payload) {
        final ChannelPublishOperation op;
        final Message reqMessage = new Message();
        reqMessage.setPayload(payload);
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelPublishOperation.newBuilder()
                                                                                             .setChannelName(name)
                                                                                             .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setChannelPublish(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }

    static public Message buildChannelHistoryMessage(String name) {
        final ChannelHistoryOperation op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelHistoryOperation.newBuilder()
                                                                                             .setChannelName(name)
                                                                                             .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setChannelHistory(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelCacheMessage(String name) {
        final ChannelCacheOperation op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelCacheOperation.newBuilder()
                                                                                           .setChannelName(name)
                                                                                           .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setChannelCache(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    static public Message buildChannelGroupHistoryMessage(String name) {
        final ChannelGroupHistoryOperation op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelGroupHistoryOperation.newBuilder()
                                                                                                  .setChannelGroupName(name)
                                                                                                  .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setChannelGroupHistory(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelGroupCacheMessage(String name) {
        final ChannelGroupCacheOperation op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelGroupCacheOperation.newBuilder()
                                                                                                .setChannelGroupName(name)
                                                                                                .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setChannelGroupCache(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelGroupDeleteMessage(String name, String payloadId) {
        final ChannelGroupDeleteOperation op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelGroupDeleteOperation.newBuilder()
                                                                                                 .setChannelGroupName(name)
                                                                                                 .setPayloadId(payloadId)
                                                                                                 .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setChannelGroupDelete(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelGroupPublishMessage(String name, Payload<?> payload) {
        final ChannelGroupPublishOperation op;
        final Message reqMessage = new Message();
        reqMessage.setPayload(payload);
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelGroupPublishOperation.newBuilder()
                                                                                                  .setChannelGroupName(name)
                                                                                                  .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setChannelGroupPublish(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelGroupCloseMessage(String name) {
        final ChannelGroupCloseOperation op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelGroupCloseOperation.newBuilder()
                                                                                                .setChannelGroupName(name)
                                                                                                .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setChannelGroupClose(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelGroupOpenMessage(String name) {
        final ChannelGroupOpenOperation op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelGroupOpenOperation.newBuilder()
                                                                                               .setChannelGroupName(name)
                                                                                               .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setChannelGroupOpen(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelGroupRemoveChannelMessage(String name, String channelName) {
        final ChannelGroupRemoveChannelOperation op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelGroupRemoveChannelOperation.newBuilder()
                                                                                                        .setChannelGroupName(name)
                                                                                                        .setChannelName(channelName)
                                                                                                        .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setChannelGroupRemoveChannel(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelGroupAddChannelMessage(String name, String channelName) {
        final ChannelGroupAddChannelOperation op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelGroupAddChannelOperation.newBuilder()
                                                                                                     .setChannelGroupName(name)
                                                                                                     .setChannelName(channelName)
                                                                                                     .build())
                                              .setOneOfOperation(OneOfOperation.newBuilder()
                                                                               .setChannelGroupAddChannel(op)
                                                                               .build())
                                              .build());
        return reqMessage;
    }
}
