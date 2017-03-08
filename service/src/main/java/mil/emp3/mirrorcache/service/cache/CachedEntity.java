package mil.emp3.mirrorcache.service.cache;

public class CachedEntity {
    static private int nextId;
    
    final private int id;
    private int version;
    
    private String payloadId;
    private String payloadType;
    private byte[] payloadData;
    
    private long createdTime;
    private long updatedTime;
    
    private CachedEntity() {
        id = ++nextId;
    }
    public CachedEntity(CachedEntity entity) {
        id          = entity.getId();
        version     = entity.getVersion() + 1;
        
        payloadId   = entity.getPayloadId();
        payloadType = entity.getPayloadType();
        payloadData = entity.getPayloadData();
        
        createdTime = entity.getCreatedTime();
        updatedTime = System.currentTimeMillis();
    }
    
    public int getId() {
        return id;
    }
    public int getVersion() {
        return version;
    }
    public String getPayloadId() {
        return payloadId;
    }
    public String getPayloadType() {
        return payloadType;
    }
    public byte[] getPayloadData() {
        return payloadData;
    }
    public long getCreatedTime() {
        return createdTime;
    }
    public long getUpdatedTime() {
        return updatedTime;
    }
    
    static public class Builder {
        private CachedEntity instance;
        
        public Builder() {
            instance = new CachedEntity();
        }

        public Builder setPayloadId(String payloadId) {
            instance.payloadId = payloadId;
            return this;
        }
        public Builder setPayloadType(String payloadType) {
            instance.payloadType = payloadType;
            return this;
        }
        public Builder setPayloadData(byte[] payloadData) {
            instance.payloadData = payloadData;
            return this;
        }
        
        public CachedEntity build() {
            instance.updatedTime = instance.createdTime = System.currentTimeMillis();
            return instance;
        }
    }
}
