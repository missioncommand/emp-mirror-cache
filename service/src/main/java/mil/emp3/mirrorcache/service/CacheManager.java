package mil.emp3.mirrorcache.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.cmapi.primitives.proto.CmapiProto.CacheInfo;
import org.cmapi.primitives.proto.CmapiProto.EntityInfo;

import mil.emp3.mirrorcache.service.cache.CachedEntity;
import mil.emp3.mirrorcache.service.cache.EntityCache;
import mil.emp3.mirrorcache.service.event.ChannelDeletedEvent;
import mil.emp3.mirrorcache.service.event.ChannelGroupDeletedEvent;

/**
 * This class maintains a cache of each entity published to Channels and ChannelGroups.
 * This class can be used to retrieve all entities published to a particular Channel or
 * ChannelGroup.
 */
@ApplicationScoped
public class CacheManager {

    private EntityCache entityCache;
    private Map<String, ServerChannelCache> channelCacheMap;           // map<channelName, channelCache>
    private Map<String, ServerChannelGroupCache> channelGroupCacheMap; // map<channelGroupName, channelGroupCache>
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    @PostConstruct
    public void init() {
        entityCache          = new EntityCache();
        channelCacheMap      = new HashMap<>();
        channelGroupCacheMap = new HashMap<>();
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    public void onChannelDeleted(@Observes ChannelDeletedEvent event) {
        final ServerChannel deletedChannel = (ServerChannel) event.getChannel();

        /*
         * Remove channel from cache.
         */
        if (channelCacheMap.containsKey(deletedChannel.getName())) {
            channelCacheMap.remove(deletedChannel.getName());
        }
    }
    
    public void onChannelGroupDeleted(@Observes ChannelGroupDeletedEvent event) {
        final ServerChannelGroup deletedChannelGroup = (ServerChannelGroup) event.getChannelGroup();

        /*
         * Remove channelGroup from cache.
         */
        if (channelGroupCacheMap.containsKey(deletedChannelGroup.getName())) {
            channelGroupCacheMap.remove(deletedChannelGroup.getName());
        }
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    public void addToChannelCache(String sessionId, String channelName, CachedEntity entity) {
        final ServerChannelCache cache = getChannelCache(sessionId, channelName);
        cache.addEntityId(entity.getId());
    }
    
    public void addToChannelGroupCache(String sessionId, String channelGroupName, CachedEntity entity) {
        final ServerChannelGroupCache cache = getChannelGroupCache(sessionId, channelGroupName);
        cache.addEntityId(entity.getId());
    }
    
    public ServerChannelCache getChannelCache(String sessionId, String channelName) {
        ServerChannelCache cache = channelCacheMap.get(channelName);
        if (cache == null) {
            cache = new ServerChannelCache(channelName);
            channelCacheMap.put(channelName, cache);
        }
        return cache;
    }
    
    public ServerChannelGroupCache getChannelGroupCache(String sessionId, String channelGroupName) {
        ServerChannelGroupCache cache = channelGroupCacheMap.get(channelGroupName);
        if (cache == null) {
            cache = new ServerChannelGroupCache(channelGroupName);
            channelGroupCacheMap.put(channelGroupName, cache);
        }
        return cache;
    }
    
    public EntityCache getEntityCache() {
        return entityCache;
    }

    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    public List<EntityInfo> statusEntity() {
        final List<EntityInfo> status = new ArrayList<>();
        
        for (CachedEntity entity : entityCache.getEntities()) {
            status.add(EntityInfo.newBuilder()
                                 .setId(entity.getId())
                                 .setVersion(entity.getVersion())
                                 .setPayloadId(entity.getPayloadId())
                                 .setPayloadType(entity.getPayloadType())
                                 .setCreatedTime(entity.getCreatedTime())
                                 .setUpdatedTime(entity.getUpdatedTime())
                                 .build());
        }
        
        return status;
    }
    
    public List<CacheInfo> statusChannel() {
        final List<CacheInfo> status = new ArrayList<>();
        
        for (Entry<String, ServerChannelCache> entry : channelCacheMap.entrySet()) {
            status.add(CacheInfo.newBuilder()
                                .setName(entry.getKey())
                                .addAllEntityId(new ArrayList<Integer>(entry.getValue().getEntityIds()))
                                .build());
        }
        
        return status;
    }
    
    public List<CacheInfo> statusChannelGroup() {
        final List<CacheInfo> status = new ArrayList<>();
        
        for (Entry<String, ServerChannelGroupCache> entry : channelGroupCacheMap.entrySet()) {
            status.add(CacheInfo.newBuilder()
                                .setName(entry.getKey())
                                .addAllEntityId(new ArrayList<Integer>(entry.getValue().getEntityIds()))
                                .build());
        }
        
        return status;
    }
}
