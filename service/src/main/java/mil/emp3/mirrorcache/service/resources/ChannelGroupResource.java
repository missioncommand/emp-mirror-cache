package mil.emp3.mirrorcache.service.resources;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cmapi.primitives.proto.CmapiProto.ChannelGroupInfo;
import org.cmapi.primitives.proto.CmapiProto.ChannelInfo;
import org.cmapi.primitives.proto.CmapiProto.MemberInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import mil.emp3.mirrorcache.service.ChannelGroupManager;

@Stateless
@Path("channelgroups")
@Produces(MediaType.APPLICATION_JSON)
public class ChannelGroupResource {

    @Inject
    private ChannelGroupManager channelGroupManager;
    
    
    @GET
    public String channelGroups() {
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
        
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonChannelGroupsArray);
    }
}