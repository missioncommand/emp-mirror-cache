package mil.emp3.mirrorcache.channel;

import org.cmapi.primitives.proto.CmapiProto.OneOfCommand;

import mil.emp3.mirrorcache.History;

/**
 * A history of channelGroup commands.
 */
public interface ChannelGroupHistory extends History<OneOfCommand> {

}
