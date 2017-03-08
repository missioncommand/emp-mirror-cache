package mil.emp3.mirrorcache.service.resources;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cmapi.primitives.proto.CmapiProto.QueueInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import mil.emp3.mirrorcache.service.SessionManager;

@Stateless
@Path("queues")
@Produces(MediaType.APPLICATION_JSON)
public class QueueStatusResource {

    @Inject
    private SessionManager sessionManager;
    
    @GET
    @Path("{name}")
    public String status(@PathParam("name") String name) {
        final JsonArray jsonEntryInfoArray = new JsonArray();

        for (QueueInfo info : sessionManager.statusQueues()) {
            if (info.getQueueName().equals(name)) {
                for (String entry : info.getEntryList()) {
                    final JsonObject jsonEntryInfo = new JsonObject();
                    jsonEntryInfo.addProperty("entry", entry);
    
                    jsonEntryInfoArray.add(jsonEntryInfo);
                }
                break;
            }
        }
        
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonEntryInfoArray);
    }
    
    @GET
    public String status() {
        final JsonArray jsonQueueInfoArray = new JsonArray();
        for (QueueInfo info : sessionManager.statusQueues()) {
            final JsonObject jsonQueueInfo = new JsonObject();
            jsonQueueInfo.addProperty("name", info.getQueueName());
            
            final JsonArray jsonEntryInfoArray = new JsonArray();
            for (String entry : info.getEntryList()) {
                final JsonObject jsonEntryInfo = new JsonObject();
                jsonEntryInfo.addProperty("entry", entry);

                jsonEntryInfoArray.add(jsonEntryInfo);
            }
            jsonQueueInfo.add("entries", jsonEntryInfoArray);

            jsonQueueInfoArray.add(jsonQueueInfo);
        }
        final JsonObject jsonSessionManager = new JsonObject();
        jsonSessionManager.add("queues", jsonQueueInfoArray);
        
        final JsonObject json = new JsonObject();
        json.add("outboundQueues", jsonSessionManager);
        
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }
}
