package mil.emp3.mirrorcache.event;

/**
 * These are reactive events fired as a result of messages 
 * received by the server. These events do not get fired by the
 * local client.
 */
public abstract class ChannelEvent<T extends ChannelEventHandler> extends MirrorCacheEvent<T> {
    
    final private String channelName;
    
    public ChannelEvent(String channelName) {
        this.channelName = channelName;
    }
    
    public String getChannelName() {
        return channelName;
    }
}
