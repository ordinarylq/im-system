package com.lq.im.codec.config;

import com.alibaba.fastjson.JSONObject;
import com.lq.im.codec.body.OfflineNotificationMessageBody;
import com.lq.im.codec.proto.ImServiceMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

public class MessageEncoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] data = JSONObject.toJSONString(msg).getBytes(StandardCharsets.UTF_8);
        if (msg instanceof OfflineNotificationMessageBody) {
            OfflineNotificationMessageBody<?> messageBody = (OfflineNotificationMessageBody) msg;
            out.writeInt(messageBody.getCommand());
            out.writeInt(data.length);
            out.writeBytes(data);
        } else if (msg instanceof ImServiceMessage) {
            ImServiceMessage message = (ImServiceMessage) msg;
            out.writeInt(message.getCommand());
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
