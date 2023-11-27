package com.lq.im.codec.config;

import com.alibaba.fastjson.JSONObject;
import com.lq.im.codec.proto.Message;
import com.lq.im.codec.proto.MessageHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 28) {
            in.resetReaderIndex();
            return;
        }
        int command = in.readInt();
        int version = in.readInt();
        int clientType = in.readInt();
        int appId = in.readInt();
        int messageBodyType = in.readInt();
        int imeiLength = in.readInt();
        int messageBodyLength = in.readInt();
        if (in.readableBytes() < imeiLength + messageBodyLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] imeiData = new byte[imeiLength];
        in.readBytes(imeiData);

        byte[] bodyData = new byte[messageBodyLength];
        in.readBytes(bodyData);

        MessageHeader messageHeader =
                new MessageHeader(command, version, clientType, appId, messageBodyType, imeiLength, messageBodyLength,
                        new String(imeiData, StandardCharsets.UTF_8));
        Message message = new Message();
        message.setHeader(messageHeader);
        if (messageBodyType == 0x0) {
            // json类型
            JSONObject jsonObject = JSONObject.parseObject(new String(bodyData));
            message.setBody(jsonObject);
        }
        out.add(message);
        in.markReaderIndex();
    }
}
