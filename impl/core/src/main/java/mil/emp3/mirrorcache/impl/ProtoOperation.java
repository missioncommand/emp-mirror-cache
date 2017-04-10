package mil.emp3.mirrorcache.impl;

import java.util.Objects;

import org.cmapi.primitives.proto.CmapiProto.OneOfOperation;

import mil.emp3.mirrorcache.Operation;

public class ProtoOperation implements Operation {

    private Object protoOperation; //TODO may not need this; consider removing
    private OneOfOperation oneOfOperation;
    
    @Override
    public String name() {
        return oneOfOperation.getOperationCase().name();
    }
    
    @Override
    public <T> T as(Class<T> type) {
        if (type.isInstance(protoOperation)) {
            return type.cast(protoOperation);
            
        } else if (type.isInstance(oneOfOperation)) {
            return type.cast(oneOfOperation);
            
        } else {
            throw new IllegalStateException("Incompatible type: " + type);
        }
    }

    @Override
    public String toString() {
        return oneOfOperation.toString();
    }
    
    static public Builder newBuilder() {
        return new Builder();
    }
    
    static public class Builder {
        private ProtoOperation instance;
        
        private Builder() {
            instance = new ProtoOperation();
        }
        
        public Builder setOneOfOperation(OneOfOperation oneOfOperation) {
            instance.oneOfOperation = oneOfOperation;
            return this;
        }
        
        public Builder setProtoOperation(Object protoOperation) {
            instance.protoOperation = protoOperation;
            return this;
        }
        
        public Operation build() {
            Objects.requireNonNull(instance.oneOfOperation, "must specify OneOfOperation");
            return instance;
        }
    }

}
