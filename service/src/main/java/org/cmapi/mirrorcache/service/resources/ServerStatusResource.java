package org.cmapi.mirrorcache.service.resources;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cmapi.mirrorcache.service.ChannelGroupManager;
import org.cmapi.mirrorcache.service.ChannelManager;
import org.cmapi.mirrorcache.service.SessionManager;
import org.cmapi.mirrorcache.service.entity.SessionInfo;
import org.cmapi.primitives.proto.CmapiProto.ChannelGroupInfo;
import org.cmapi.primitives.proto.CmapiProto.ChannelInfo;
import org.cmapi.primitives.proto.CmapiProto.MemberInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Stateless
@Path("status")
@Produces(MediaType.APPLICATION_JSON)
public class ServerStatusResource {

    @Inject
    private ChannelGroupManager channelGroupManager;
    
    @Inject
    private ChannelManager channelManager;
    
    @Inject
    private SessionManager sessionManager;
    
    @GET
    public String status() {
        // channelGroupManager - channelGroups
        final JsonArray jsonChannelGroupsArray = new JsonArray();
        for (ChannelGroupInfo channelInfo : channelGroupManager.statusChannelGroups()) {
            final JsonObject jsonChannelGroupInfoArray = new JsonObject();
            jsonChannelGroupInfoArray.addProperty("name", channelInfo.getName());

            final JsonArray jsonChannelInfoArray = new JsonArray();
            for (ChannelInfo info : channelInfo.getChannelList()) {
                final JsonObject jsonChannelInfo = new JsonObject();
                jsonChannelInfo.addProperty("name", info.getName());
                
                jsonChannelInfoArray.add(jsonChannelInfo);
            }
            jsonChannelGroupInfoArray.add("channels", jsonChannelInfoArray);
            
            final JsonArray jsonMemberInfoArray = new JsonArray();
            for (MemberInfo info : channelInfo.getMemberList()) {
                final JsonObject jsonMemberInfo = new JsonObject();
                jsonMemberInfo.addProperty("sessionId", info.getSessionId());

                jsonMemberInfoArray.add(jsonMemberInfo);
            }
            jsonChannelGroupInfoArray.add("members", jsonMemberInfoArray);
            
            jsonChannelGroupsArray.add(jsonChannelGroupInfoArray);
        }
        final JsonObject jsonChannelGroupManager = new JsonObject();
        jsonChannelGroupManager.add("channelGroups", jsonChannelGroupsArray);
        
        
        // channelManager
        final JsonArray jsonChannelInfoArray = new JsonArray();
        for (ChannelInfo channelInfo : channelManager.statusChannels()) {
            final JsonObject jsonChannelInfo = new JsonObject();
            jsonChannelInfo.addProperty("name", channelInfo.getName());
            jsonChannelInfo.addProperty("type", channelInfo.getType());
            jsonChannelInfo.addProperty("visibility", channelInfo.getVisibility());
            
            final JsonArray jsonMemberInfoArray = new JsonArray();
            for (MemberInfo info : channelInfo.getMemberList()) {
                final JsonObject jsonMemberInfo = new JsonObject();
                jsonMemberInfo.addProperty("sessionId", info.getSessionId());

                jsonMemberInfoArray.add(jsonMemberInfo);
            }
            jsonChannelInfo.add("members", jsonMemberInfoArray);
            
            
            
            jsonChannelInfoArray.add(jsonChannelInfo);
        }
        
        final JsonObject jsonChannelManager = new JsonObject();
        jsonChannelManager.add("channels", jsonChannelInfoArray);
        
        
        // sessionManager
        final JsonArray jsonSessionInfoArray = new JsonArray();
        for (SessionInfo info : sessionManager.status()) {
            final JsonObject jsonSessionInfo = new JsonObject();
            jsonSessionInfo.addProperty("sessionId", info.getSessionId());
            jsonSessionInfo.addProperty("agent", info.getAgent());
            jsonSessionInfo.addProperty("outboundQueueSize", info.getOutboundQueueSize());

            jsonSessionInfoArray.add(jsonSessionInfo);
        }
        final JsonObject jsonSessionManager = new JsonObject();
        jsonSessionManager.add("sessions", jsonSessionInfoArray);
        
        final JsonObject json = new JsonObject();
        json.add("channelGroupManager", jsonChannelGroupManager);
        json.add("channelManager"     , jsonChannelManager);
        json.add("sessionManager"     , jsonSessionManager);
        
        
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }
}
