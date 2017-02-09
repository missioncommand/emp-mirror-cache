package org.cmapi.mirrorcache.channel;

import org.cmapi.mirrorcache.History;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand;

/**
 * A history of channel commands.
 */
public interface ChannelHistory extends History<OneOfCommand> {
    
}
