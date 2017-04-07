package mil.emp3.mirrorcache.impl;

import org.cmapi.primitives.proto.CmapiProto.ChannelCacheCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelCloseCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelDeleteCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupAddChannelCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupCacheCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupCloseCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupDeleteCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupHistoryCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupOpenCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupPublishCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupRemoveChannelCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelHistoryCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelOpenCommand;
import org.cmapi.primitives.proto.CmapiProto.ChannelPublishCommand;
import org.cmapi.primitives.proto.CmapiProto.CreateChannelCommand;
import org.cmapi.primitives.proto.CmapiProto.CreateChannelGroupCommand;
import org.cmapi.primitives.proto.CmapiProto.DeleteChannelCommand;
import org.cmapi.primitives.proto.CmapiProto.DeleteChannelGroupCommand;
import org.cmapi.primitives.proto.CmapiProto.FindChannelGroupsCommand;
import org.cmapi.primitives.proto.CmapiProto.FindChannelsCommand;
import org.cmapi.primitives.proto.CmapiProto.GetClientInfoCommmand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.Payload;
import mil.emp3.mirrorcache.channel.Channel;
import mil.emp3.mirrorcache.channel.Channel.Flow;

public class MessageBuilder {
    private MessageBuilder() { }
    
    static public Message buildFindChannelGroupsMessage(String filter) {
        final FindChannelGroupsCommand op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = FindChannelGroupsCommand.newBuilder()
                                                                                              .setFilter(filter)
                                                                                              .build())
                                              .setOneOfCommand(OneOfCommand.newBuilder()
                                                                           .setFindChannelGroups(op)
                                                                           .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildDeleteChannelGroupMessage(String name) {
        final DeleteChannelGroupCommand op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = DeleteChannelGroupCommand.newBuilder()
                                                                                               .setChannelGroupName(name)
                                                                                               .build())
                                              .setOneOfCommand(OneOfCommand.newBuilder()
                                                                           .setDeleteChannelGroup(op)
                                                                           .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildCreateChannelGroupMessage(String name) {
        final CreateChannelGroupCommand op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = CreateChannelGroupCommand.newBuilder()
                                                                                               .setChannelGroupName(name)
                                                                                               .build())
                                              .setOneOfCommand(OneOfCommand.newBuilder()
                                                                           .setCreateChannelGroup(op)
                                                                           .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildFindChannelsMessage(String filter) {
        final FindChannelsCommand op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = FindChannelsCommand.newBuilder()
                                                                                         .setFilter(filter)
                                                                                         .build())
                                              .setOneOfCommand(OneOfCommand.newBuilder()
                                                                           .setFindChannels(op)
                                                                           .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildDeleteChannelMessage(String name) {
        final DeleteChannelCommand op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = DeleteChannelCommand.newBuilder()
                                                                                          .setChannelName(name)
                                                                                          .build())
                                              .setOneOfCommand(OneOfCommand.newBuilder()
                                                                           .setDeleteChannel(op)
                                                                           .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildCreateChannelMessage(String name, Channel.Visibility visibility, Channel.Type type) {
        final CreateChannelCommand op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = CreateChannelCommand.newBuilder()
                                                                                          .setChannelName(name)
                                                                                          .setType(type.name())
                                                                                          .setVisibility(visibility.name())
                                                                                          .build())
                                              .setOneOfCommand(OneOfCommand.newBuilder()
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
                                              .setOneOfCommand(OneOfCommand.newBuilder()
                                                                           .setGetClientInfo(op)
                                                                           .build())
                                              .build());
        return reqMessage;
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    static public Message buildChannelCloseMessage(String name) {
        final ChannelCloseCommand op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelCloseCommand.newBuilder()
                                                                                         .setChannelName(name)
                                                                                         .build())
                                              .setOneOfCommand(OneOfCommand.newBuilder()
                                                                           .setChannelClose(op)
                                                                           .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelOpenMessage(String name, Flow flow, String filter) {
        final ChannelOpenCommand op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelOpenCommand.newBuilder()
                                                                                        .setChannelName(name)
                                                                                        .setFlow(flow.name())
                                                                                        .setFilter(filter)
                                                                                        .build())
                                              .setOneOfCommand(OneOfCommand.newBuilder()
                                                                           .setChannelOpen(op)
                                                                           .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelDeleteMessage(String name, String payloadId) {
        final ChannelDeleteCommand op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelDeleteCommand.newBuilder()
                                                                                          .setChannelName(name)
                                                                                          .setPayloadId(payloadId)
                                                                                          .build())
                                              .setOneOfCommand(OneOfCommand.newBuilder()
                                                                           .setChannelDelete(op)
                                                                           .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelPublishMessage(String name, Payload<?> payload) {
        final ChannelPublishCommand op;
        final Message reqMessage = new Message();
        reqMessage.setPayload(payload);
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelPublishCommand.newBuilder()
                                                                                           .setChannelName(name)
                                                                                           .build())
                                              .setOneOfCommand(OneOfCommand.newBuilder()
                                                                           .setChannelPublish(op)
                                                                           .build())
                                              .build());
        return reqMessage;
    }

    static public Message buildChannelHistoryMessage(String name) {
        final ChannelHistoryCommand op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelHistoryCommand.newBuilder()
                                                                                           .setChannelName(name)
                                                                                           .build())
                                              .setOneOfCommand(OneOfCommand.newBuilder()
                                                                           .setChannelHistory(op)
                                                                           .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelCacheMessage(String name) {
        final ChannelCacheCommand op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelCacheCommand.newBuilder()
                                                                                         .setChannelName(name)
                                                                                         .build())
                                              .setOneOfCommand(OneOfCommand.newBuilder()
                                                                           .setChannelCache(op)
                                                                           .build())
                                              .build());
        return reqMessage;
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    static public Message buildChannelGroupHistoryMessage(String name) {
        final ChannelGroupHistoryCommand op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelGroupHistoryCommand.newBuilder()
                                                                                                .setChannelGroupName(name)
                                                                                                .build())
                                              .setOneOfCommand(OneOfCommand.newBuilder()
                                                                           .setChannelGroupHistory(op)
                                                                           .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelGroupCacheMessage(String name) {
        final ChannelGroupCacheCommand op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelGroupCacheCommand.newBuilder()
                                                                                              .setChannelGroupName(name)
                                                                                              .build())
                                              .setOneOfCommand(OneOfCommand.newBuilder()
                                                                           .setChannelGroupCache(op)
                                                                           .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelGroupDeleteMessage(String name, String payloadId) {
        final ChannelGroupDeleteCommand op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelGroupDeleteCommand.newBuilder()
                                                                                               .setChannelGroupName(name)
                                                                                               .setPayloadId(payloadId)
                                                                                               .build())
                                              .setOneOfCommand(OneOfCommand.newBuilder()
                                                                           .setChannelGroupDelete(op)
                                                                           .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelGroupPublishMessage(String name, Payload<?> payload) {
        final ChannelGroupPublishCommand op;
        final Message reqMessage = new Message();
        reqMessage.setPayload(payload);
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelGroupPublishCommand.newBuilder()
                                                                                                .setChannelGroupName(name)
                                                                                                .build())
                                              .setOneOfCommand(OneOfCommand.newBuilder()
                                                                           .setChannelGroupPublish(op)
                                                                           .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelGroupCloseMessage(String name) {
        final ChannelGroupCloseCommand op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelGroupCloseCommand.newBuilder()
                                                                                              .setChannelGroupName(name)
                                                                                              .build())
                                              .setOneOfCommand(OneOfCommand.newBuilder()
                                                                           .setChannelGroupClose(op)
                                                                           .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelGroupOpenMessage(String name) {
        final ChannelGroupOpenCommand op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelGroupOpenCommand.newBuilder()
                                                                                             .setChannelGroupName(name)
                                                                                             .build())
                                              .setOneOfCommand(OneOfCommand.newBuilder()
                                                                           .setChannelGroupOpen(op)
                                                                           .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelGroupRemoveChannelMessage(String name, String channelName) {
        final ChannelGroupRemoveChannelCommand op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelGroupRemoveChannelCommand.newBuilder()
                                                                                                      .setChannelGroupName(name)
                                                                                                      .setChannelName(channelName)
                                                                                                      .build())
                                              .setOneOfCommand(OneOfCommand.newBuilder()
                                                                           .setChannelGroupRemoveChannel(op)
                                                                           .build())
                                              .build());
        return reqMessage;
    }
    
    static public Message buildChannelGroupAddChannelMessage(String name, String channelName) {
        final ChannelGroupAddChannelCommand op;
        final Message reqMessage = new Message();
        reqMessage.setOperation(ProtoOperation.newBuilder()
                                              .setProtoOperation(op = ChannelGroupAddChannelCommand.newBuilder()
                                                                                                   .setChannelGroupName(name)
                                                                                                   .setChannelName(channelName)
                                                                                                   .build())
                                              .setOneOfCommand(OneOfCommand.newBuilder()
                                                                           .setChannelGroupAddChannel(op)
                                                                           .build())
                                              .build());
        return reqMessage;
    }
}
