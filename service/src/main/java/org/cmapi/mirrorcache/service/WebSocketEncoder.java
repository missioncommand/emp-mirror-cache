package org.cmapi.mirrorcache.service;

import java.nio.ByteBuffer;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.cmapi.primitives.proto.CmapiProto.ProtoMessage;

public class WebSocketEncoder implements Encoder.Binary<ProtoMessage> {

    @Override
    public void init(EndpointConfig arg0) {
    }
    @Override
    public void destroy() {
    }

    @Override
    public ByteBuffer encode(ProtoMessage res) throws EncodeException {

        return ByteBuffer.wrap(res.toByteArray());
    }

}
