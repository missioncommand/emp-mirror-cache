package mil.emp3.mirrorcache.service.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cmapi.primitives.proto.CmapiProto.ProtoPayload;

public class EntityCache {

    final private Map<String, CachedEntity> entityMap; // map<payloadId, entity>
    
    public EntityCache() {
        this.entityMap = new HashMap<>();
    }
    
    public List<Integer> getEntityIds() {
        final List<Integer> entityIds = new ArrayList<>();
        for (Iterator<CachedEntity> iter = entityMap.values().iterator(); iter.hasNext(); ) {
            entityIds.add(iter.next().getId());
        }
        return entityIds;
    }
    
    public List<CachedEntity> getEntities() {
        final List<CachedEntity> entities = new ArrayList<>(entityMap.values());
        return entities;
    }
    
    public CachedEntity getEntityByEntityId(int entityId) {
        for (Iterator<CachedEntity> iter = entityMap.values().iterator(); iter.hasNext(); ) {
            final CachedEntity entity = iter.next();
            if (entity.getId() == entityId) { 
                return entity;
            }
        }
        throw new IllegalArgumentException("Entity not found for entityId: " + entityId);
    }
    
    public CachedEntity getEntityByPayloadId(String payloadId) {
        if (!entityMap.containsKey(payloadId)) {
            throw new IllegalArgumentException("Entity not found for payloadId: " + payloadId);
        }
        
        final CachedEntity entity = entityMap.get(payloadId);
        return entity;
    }
    
    public CachedEntity update(ProtoPayload payload) {
        final String payloadId   = payload.getId();
        final String payloadType = payload.getType();
        final byte[] payloadData = payload.getData().toByteArray();
        
        CachedEntity entity;
        
        if (entityMap.containsKey(payloadId)) { // update existing entity
            entityMap.put(payloadId, new CachedEntity(entity = entityMap.get(payloadId)));
            
        } else { // create new entity
            entityMap.put(payloadId, entity = new CachedEntity.Builder()
                                                              .setPayloadId(payloadId)
                                                              .setPayloadType(payloadType)
                                                              .setPayloadData(payloadData)
                                                              .build());
        }
        return entity;
    }

}
