package mil.emp3.mirrorcache.channel;

import org.cmapi.primitives.proto.CmapiProto.OneOfCommand;

import mil.emp3.mirrorcache.History;

/**
 * A history of channel commands.
 */
public interface ChannelHistory extends History<OneOfCommand> {
    
}
