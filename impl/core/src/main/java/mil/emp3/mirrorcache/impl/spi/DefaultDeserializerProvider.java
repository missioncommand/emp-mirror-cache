package mil.emp3.mirrorcache.impl.spi;

import org.cmapi.primitives.proto.CmapiProto.Container;
import org.cmapi.primitives.proto.CmapiProto.MilStdSymbol;

import com.google.protobuf.InvalidProtocolBufferException;

import mil.emp3.mirrorcache.Deserializer;
import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Payload;
import mil.emp3.mirrorcache.spi.DeserializerProvider;

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
