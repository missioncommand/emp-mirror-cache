package mil.emp3.mirrorcache.impl.request;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cmapi.primitives.proto.CmapiProto.ChannelGroupInfo;
import org.cmapi.primitives.proto.CmapiProto.ChannelInfo;
import org.cmapi.primitives.proto.CmapiProto.FindChannelGroupsCommand;
import org.cmapi.primitives.proto.CmapiProto.MemberInfo;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand.CommandCase;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.Member;
import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MessageDispatcher;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Priority;
import mil.emp3.mirrorcache.channel.Channel.Type;
import mil.emp3.mirrorcache.channel.Channel.Visibility;
import mil.emp3.mirrorcache.channel.ChannelGroup;
import mil.emp3.mirrorcache.impl.channel.ClientChannel;
import mil.emp3.mirrorcache.impl.channel.ClientChannelGroup;

public class FindChannelGroupsRequestProcessor extends BaseRequestProcessor<Message, List<ChannelGroup>> {
    static final private Logger LOG = LoggerFactory.getLogger(FindChannelGroupsRequestProcessor.class);
    
    final private MessageDispatcher dispatcher;
    
    public FindChannelGroupsRequestProcessor(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
    
    @Override
    public List<ChannelGroup> executeSync(Message reqMessage) throws MirrorCacheException {
        if (reqMessage == null) {
            throw new IllegalStateException("reqMessage == null");
        }
        
        dispatcher.dispatchMessage(reqMessage.setPriority(Priority.MEDIUM));

        try {
            final Message resMessage = dispatcher.awaitResponse(reqMessage);
            
            final FindChannelGroupsCommand command = resMessage.getCommand(CommandCase.FIND_CHANNEL_GROUPS);
            if (command.getStatus() == Status.SUCCESS) {
                final List<ChannelGroup> results = new ArrayList<>();

                for (ChannelGroupInfo channelGroup : command.getChannelGroupList()) {
                    
                    // channels
                    final Set<ClientChannel> channels = new HashSet<>();
                    for (ChannelInfo channelInfo : channelGroup.getChannelList()) {
                        channels.add(new ClientChannel(channelInfo.getName(),
                                          Visibility.valueOf(channelInfo.getVisibility()),
                                          Type.valueOf(channelInfo.getType()),
                                          channelInfo.getIsOpen(),
                                          dispatcher));
                    }
                    
                    // members
                    final Set<Member> members = new HashSet<>();
                    for (MemberInfo memberInfo : channelGroup.getMemberList()) {
                        members.add(new Member(memberInfo.getSessionId()));
                    }
                    
                    results.add(new ClientChannelGroup(channelGroup.getName(),
                                                       channelGroup.getIsJoined(),
                                                       channels,
                                                       members,
                                                       dispatcher));
                }
                
                return results;
                
            } else {
                throw new MirrorCacheException(Reason.FIND_CHANNELGROUPS_FAILURE).withDetail("filter: " + command.getFilter());
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.info(Thread.currentThread().getName() + " thread interrupted.");
        }
        
        throw new IllegalStateException(new MirrorCacheException(Reason.UNKNOWN));
    }

}
