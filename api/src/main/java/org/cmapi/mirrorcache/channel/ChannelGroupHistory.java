package org.cmapi.mirrorcache.channel;

import org.cmapi.mirrorcache.History;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand;

/**
 * A history of channelGroup commands.
 */
public interface ChannelGroupHistory extends History<OneOfCommand> {

}
