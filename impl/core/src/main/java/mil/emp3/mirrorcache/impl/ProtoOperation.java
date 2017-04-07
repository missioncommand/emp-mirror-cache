package mil.emp3.mirrorcache.impl;

import java.util.Objects;

import org.cmapi.primitives.proto.CmapiProto.OneOfCommand;

import mil.emp3.mirrorcache.Operation;

public class ProtoOperation implements Operation {

    private Object protoOperation;
    private OneOfCommand oneOfCommand;
    
    @Override
    public String name() {
        return oneOfCommand.getCommandCase().name();
    }
    
    @Override
    public <T> T as(Class<T> type) {
        if (type.isInstance(protoOperation)) {
            return type.cast(protoOperation);
            
        } else if (type.isInstance(oneOfCommand)) {
            return type.cast(oneOfCommand);
            
        } else {
            throw new IllegalStateException("Incompatible type: " + type);
        }
    }

    @Override
    public String toString() {
        return oneOfCommand.toString();
    }
    
    static public Builder newBuilder() {
        return new Builder();
    }
    
    static public class Builder {
        private ProtoOperation instance;
        
        private Builder() {
            instance = new ProtoOperation();
        }
        
        public Builder setOneOfCommand(OneOfCommand oneOfCommand) {
            instance.oneOfCommand = oneOfCommand;
            return this;
        }
        
        public Builder setProtoOperation(Object protoOperation) {
            instance.protoOperation = protoOperation;
            return this;
        }
        
        public Operation build() {
            Objects.requireNonNull(instance.oneOfCommand, "must specify OneOfCommand");
            return instance;
        }
    }

}
