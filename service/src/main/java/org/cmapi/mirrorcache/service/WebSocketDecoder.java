package org.cmapi.mirrorcache.service;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.cmapi.primitives.proto.CmapiProto.ProtoMessage;

import com.google.protobuf.CodedInputStream;

public class WebSocketDecoder implements Decoder.Binary<ProtoMessage> {
    
    @Override
    public void init(EndpointConfig config) {
    }
    @Override
    public void destroy() {
    }

    @Override
    public ProtoMessage decode(ByteBuffer data) throws DecodeException {
        final ProtoMessage req;
        try {
            req = ProtoMessage.parseFrom(CodedInputStream.newInstance(data));
            
        } catch (IOException e) {
            throw new DecodeException(data, "Unable to construct ProtoMessage.", e);
        }
        
        return req;
    }

    @Override
    public boolean willDecode(ByteBuffer data) {
        return true;
    }

}
