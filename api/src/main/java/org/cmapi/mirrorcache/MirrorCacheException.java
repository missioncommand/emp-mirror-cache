package org.cmapi.mirrorcache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MirrorCacheException extends Exception {

    public enum Reason {
        UNKNOWN                            ("Unknown.."),
        
        SPI_LOAD_FAILURE                   ("Unable to load provider."),
        
        TRANSPORT_FAILURE                  ("Unable to transport."),
        TRANSLATION_FAILURE                ("Unable to translate."),
        SERIALIZATION_FAILURE              ("Unable to serialize."),
        DESERIALIZATION_FAILURE            ("Unable to deserialize."),
        
        CONNECT_FAILURE                    ("Unable to connect."),
        DISCONNECT_FAILURE                 ("Unable to disconnect."),
        
        QUEUE_POLL_TIMEOUT                 ("Timeout while polling from queue."),
        QUEUE_OFFER_TIMEOUT                ("Timeout while offering to queue."),
        QUEUE_UNEXPECTED_ID                ("Unexpected id."),
        
        // channels
        CHANNEL_DNE                        ("Channel does not exist."),
        CHANNEL_ALREADY_EXISTS             ("Channel already exists."),
        CHANNEL_ALREADY_OPEN               ("Channel already open."),
        CHANNEL_NOT_OPEN                   ("Channel not open."),
        CHANNEL_OPEN_FAILURE               ("Unable to open channel."),
        CHANNEL_CLOSE_FAILURE              ("Unable to close channel."),
        CHANNEL_CACHE_FAILURE              ("Unable to get channel cache."),
        CHANNEL_HISTORY_FAILURE            ("Unable to get channel history."),
        EGRESS_PIPELINE_SESSION_NOT_FOUND  ("Egress pipeline not found for current session."),
        
        CREATE_CHANNEL_FAILURE             ("Unable to create channel."),
        DELETE_CHANNEL_FAILURE             ("Unable to delete channel."),
        FIND_CHANNELS_FAILURE              ("Unable to find channels."),
        
        CHANNEL_MEMBER_NOT_FOUND           ("Member not a part of channel."),
        CHANNEL_MEMBER_ALREADY_EXISTS      ("Member already a part of channel."),
        
        
        
        // channelGroups
        CHANNELGROUP_DNE                    ("ChannelGroup does not exist."),
        CHANNELGROUP_ALREADY_EXISTS         ("ChannelGroup already exists."),
        CHANNELGROUP_ALREADY_JOINED         ("ChannelGroup already joined."),
        CHANNELGROUP_NOT_JOINED             ("ChannelGroup not joined."),
        CHANNELGROUP_JOIN_FAILURE           ("Unable to join channelGroup."),
        CHANNELGROUP_LEAVE_FAILURE          ("Unable to leave channelGroup."),
        CHANNELGROUP_ADD_CHANNEL_FAILURE    ("Unable to add channel to channelGroup."),
        CHANNELGROUP_REMOVE_CHANNEL_FAILURE ("Unable to remove channel from channelGroup."),
        CHANNELGROUP_CACHE_FAILURE          ("Unable to get channelGroup cache."),
        CHANNELGROUP_HISTORY_FAILURE        ("Unable to get channelGroup history."),
        
        CREATE_CHANNELGROUP_FAILURE         ("Unable to create channelGroup."),
        DELETE_CHANNELGROUP_FAILURE         ("Unable to delete channelGroup."),
        FIND_CHANNELGROUPS_FAILURE          ("Unable to find channelGroups."),
        
        CHANNELGROUP_CHANNEL_NOT_FOUND      ("Channel not found in channelGroup."),
        CHANNELGROUP_CHANNEL_ALREADY_EXISTS ("Channel already in channelGroup."),
        CHANNELGROUP_NOT_OWNER              ("Not channelGroup owner."),
        CHANNELGROUP_MEMBER_NOT_FOUND       ("Member not a part of channelGroup."),
        CHANNELGROUP_MEMBER_ALREADY_EXISTS  ("Member already a part of channelGroup."),
        
        ;
        
        final private String msg;
        Reason(String msg) {
            this.msg = msg;
        }
        public String getMsg() { return msg; }
    }

    final private List<String> details = new ArrayList<>();
    final private Reason reason;
    
    public MirrorCacheException(Reason reason) {
        super(reason.getMsg());
        this.reason = reason;
    }
    public MirrorCacheException(Reason reason, Throwable t) {
        super(reason.getMsg(), t);
        this.reason = reason;
    }
    
    @Override
    public String getMessage() {
        return "Reason: " + getReason().getMsg() + " Details: " + getDetails();
    }
    
    public Reason getReason() {
        return reason;
    }
    
    public List<String> getDetails() {
        return Collections.unmodifiableList(details);
    }
    
    public MirrorCacheException withDetail(String detail) {
        details.add(detail);
        return this;
    }
}
