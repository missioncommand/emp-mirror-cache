package org.cmapi.mirrorcache.service.resources;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cmapi.mirrorcache.service.CacheManager;
import org.cmapi.mirrorcache.service.cache.CachedEntity;
import org.cmapi.primitives.proto.CmapiProto.CacheInfo;
import org.cmapi.primitives.proto.CmapiProto.EntityInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Stateless
@Path("cache")
@Produces(MediaType.APPLICATION_JSON)
public class ChannelCacheResource {

    @Inject
    private CacheManager cacheManager;
    
    @GET
    @Path("/channel")
    public String channelCache() {
        final JsonArray jsonChannelCacheInfoArray = new JsonArray();
        for (CacheInfo channelCacheInfo : cacheManager.statusChannel()) {
            final JsonObject jsonChannelCacheInfo = new JsonObject();
            jsonChannelCacheInfo.addProperty("name", channelCacheInfo.getName());
            
            final JsonArray jsonEntityIdArray = new JsonArray();
            for (Integer entityId : channelCacheInfo.getEntityIdList()) {
                jsonEntityIdArray.add(entityId);
            }
            jsonChannelCacheInfo.add("entityIds", jsonEntityIdArray);
            
            jsonChannelCacheInfoArray.add(jsonChannelCacheInfo);
        }
        
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonChannelCacheInfoArray);
    }
    
    @GET
    @Path("/channelgroup")
    public String channelGroupCache() {
        final JsonArray jsonChannelGroupCacheInfoArray = new JsonArray();
        for (CacheInfo channelGroupCacheInfo : cacheManager.statusChannelGroup()) {
            final JsonObject jsonChannelGroupCacheInfo = new JsonObject();
            jsonChannelGroupCacheInfo.addProperty("name", channelGroupCacheInfo.getName());
            
            final JsonArray jsonEntityIdArray = new JsonArray();
            for (Integer entityId : channelGroupCacheInfo.getEntityIdList()) {
                jsonEntityIdArray.add(entityId);
            }
            jsonChannelGroupCacheInfo.add("entityIds", jsonEntityIdArray);
            
            jsonChannelGroupCacheInfoArray.add(jsonChannelGroupCacheInfo);
        }
        
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonChannelGroupCacheInfoArray);
    }
    
    @GET
    @Path("/entity")
    public String entityCache() {
        final JsonArray jsonEntityInfoArray = new JsonArray();
        for (EntityInfo entityInfo : cacheManager.statusEntity()) {
            final JsonObject jsonEntityInfo = new JsonObject();
            jsonEntityInfo.addProperty("id", entityInfo.getId());
            jsonEntityInfo.addProperty("version", entityInfo.getVersion());
            jsonEntityInfo.addProperty("payloadId", entityInfo.getPayloadId());
            jsonEntityInfo.addProperty("payloadType", entityInfo.getPayloadType());
            jsonEntityInfo.addProperty("createdTime", entityInfo.getCreatedTime());
            jsonEntityInfo.addProperty("udpatedTime", entityInfo.getUpdatedTime());
            
            jsonEntityInfoArray.add(jsonEntityInfo);
        }
        
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonEntityInfoArray);
    }
    
    @GET
    @Path("/entity/{entityId}")
    public String entityCache(@PathParam("entityId") int entityId) {
        final CachedEntity entity = cacheManager.getEntityCache().getEntityByEntityId(entityId);
        
        final JsonObject jsonEntityInfo = new JsonObject();
        jsonEntityInfo.addProperty("id", entity.getId());
        jsonEntityInfo.addProperty("version", entity.getVersion());
        jsonEntityInfo.addProperty("payloadId", entity.getPayloadId());
        jsonEntityInfo.addProperty("payloadType", entity.getPayloadType());
        jsonEntityInfo.addProperty("createdTime", entity.getCreatedTime());
        jsonEntityInfo.addProperty("udpatedTime", entity.getUpdatedTime());
        
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonEntityInfo);
    }
}
