package com.lq.im.codec.config;

import com.alibaba.fastjson.JSONObject;
import com.lq.im.codec.body.OfflineNotificationMessageBody;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

public class MessageEncoder extends MessageToByteEncoder<OfflineNotificationMessageBody<?>> {
    @Override
    protected void encode(ChannelHandlerContext ctx, OfflineNotificationMessageBody msg, ByteBuf out) throws Exception {
        byte[] data = JSONObject.toJSONString(msg).getBytes(StandardCharsets.UTF_8);
        out.writeInt(msg.getCommand());
        out.writeInt(data.length);
        out.writeBytes(data);
    }
}
