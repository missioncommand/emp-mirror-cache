package org.cmapi.mirrorcache.impl.spi;

import org.cmapi.mirrorcache.Deserializer;
import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.MirrorCacheException.Reason;
import org.cmapi.mirrorcache.Payload;
import org.cmapi.mirrorcache.spi.DeserializerProvider;
import org.cmapi.primitives.proto.CmapiProto.Container;
import org.cmapi.primitives.proto.CmapiProto.MilStdSymbol;

import com.google.protobuf.InvalidProtocolBufferException;

public class DefaultDeserializerProvider implements DeserializerProvider {

    final private Deserializer deserializer;
    
    public DefaultDeserializerProvider() {
        this.deserializer = new DefaultDeserializer();
    }
    
    @Override
    public boolean canHandle(String type) {
        return MilStdSymbol.class.getName().equals(type)
                || Container.class.getName().equals(type);
    }

    @Override
    public Deserializer getDeserializer() {
        return deserializer;
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    private static class DefaultDeserializer implements Deserializer {
        @Override
        public void processMessage(Message message) throws MirrorCacheException {
            final String payloadType = message.getPayload().getType();
            
            try {
                if (MilStdSymbol.class.getName().equals(payloadType)) {
                    final MilStdSymbol deserializedPayload = MilStdSymbol.parseFrom(message.getPayload(byte[].class).getData());
                    message.setPayload(new Payload<>(deserializedPayload.getGeoId(), payloadType, deserializedPayload));
                    
                } else if (Container.class.getName().equals(payloadType)) {
                    final Container deserializedPayload = Container.parseFrom(message.getPayload(byte[].class).getData());
                    message.setPayload(new Payload<>(deserializedPayload.getGeoId(), payloadType, deserializedPayload));
                    
                } else {
                    throw new IllegalArgumentException("Unsupported payloadType: " + payloadType);
                }
            
            } catch (InvalidProtocolBufferException e) {
                throw new MirrorCacheException(Reason.DESERIALIZATION_FAILURE, e)
                                              .withDetail("payloadType: " + payloadType);
            }
        }
    }
}
