package org.cmapi.mirrorcache.impl.stage;

import org.cmapi.mirrorcache.Message;
import org.cmapi.mirrorcache.MirrorCacheException;
import org.cmapi.mirrorcache.Payload;
import org.cmapi.mirrorcache.support.Utils;
import org.cmapi.primitives.proto.CmapiProto.ProtoMessage;
import org.cmapi.primitives.proto.CmapiProto.ProtoPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;

public class OutTransportStageProcessor extends StageProcessor<Message> {
    static final private Logger LOG = LoggerFactory.getLogger(OutTransportStageProcessor.class);
    
    public OutTransportStageProcessor() {
        super(Stage.OUT_TRANSPORT);
    }
    
    @Override
    public void processMessage(Message message) throws MirrorCacheException {
        LOG.trace("processMessage()");
        
        final ProtoMessage.Builder builder = ProtoMessage.newBuilder()
                                                         .setId(message.getId())
                                                         .setPriority(message.getPriority().getValue())
                                                         .setCommand(message.getCommand())
                                                         .putAllProperty(message.getProperties());

        if (message.hasPayload()) {
            builder.setPayload(ProtoPayload.newBuilder()
                                           .setId(message.getPayload().getId())
                                           .setType(message.getPayload().getType())
                                           .setData(ByteString.copyFrom(message.getPayload(byte[].class).getData())));
        }
        
        final ProtoMessage pm = builder.build();
        final byte[] payload  = pm.toByteArray();
        message.setPayload(new Payload<>(null, "[B", payload));
        
        LOG.debug("outMessage(" + payload.length + "): " + Utils.asString(pm));
    }

}
