package org.cmapi.mirrorcache.service.resources;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cmapi.mirrorcache.service.ChannelManager;
import org.cmapi.primitives.proto.CmapiProto.ChannelInfo;
import org.cmapi.primitives.proto.CmapiProto.MemberInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Stateless
@Path("channels")
@Produces(MediaType.APPLICATION_JSON)
public class ChannelResource {

    @Inject
    private ChannelManager channelManager;
    
    
    @GET
    public String channels() {
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
        
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonChannelInfoArray);
    }
}
