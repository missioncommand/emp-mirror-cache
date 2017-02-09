package org.cmapi.mirrorcache.impl.spi;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.Payload;
import org.cmapi.mirrorcache.Serializer;
import org.cmapi.mirrorcache.spi.SerializerProvider;
import org.cmapi.primitives.proto.CmapiProto.Container;
import org.cmapi.primitives.proto.CmapiProto.MilStdSymbol;

public class DefaultSerializerProvider implements SerializerProvider {

    final private Serializer serializer;
    
    public DefaultSerializerProvider() {
        this.serializer = new DefaultSerializer();
    }
    
    @Override
    public boolean canHandle(String type) {
        return MilStdSymbol.class.getName().equals(type)
                || Container.class.getName().equals(type);
    }

    @Override
    public Serializer getSerializer() {
        return serializer;
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    private static class DefaultSerializer implements Serializer {
        @Override
        public void processMessage(Message message) throws MirrorCacheException {
            final Object payload = message.getPayload().getData();
            
            if (payload instanceof com.google.protobuf.Message) {
                final byte[] serializedPayload = ((com.google.protobuf.Message) payload).toByteArray();
                
                message.setPayload(new Payload<>(message.getPayload().getId(), payload.getClass().getName(), serializedPayload));
                
            } else {
                throw new IllegalArgumentException("Unsupported payloadType: " + message.getPayload().getType());
            }
        }
    }
}