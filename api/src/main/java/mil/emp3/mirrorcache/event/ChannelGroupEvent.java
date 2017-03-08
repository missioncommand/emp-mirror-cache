package mil.emp3.mirrorcache.event;

/**
 * These are reactive events fired as a result of messages 
 * received by the server. These events do not get fired by the
 * local client.
 */
public abstract class ChannelGroupEvent<T extends ChannelGroupEventHandler> extends MirrorCacheEvent<T> {
    
    final private String channelGroupName;
    
    public ChannelGroupEvent(String channelGroupName) {
        this.channelGroupName = channelGroupName;
    }
    
    public String getChannelGroupName() {
        return channelGroupName;
    }
}
