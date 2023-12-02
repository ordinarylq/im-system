package com.lq.im.tcp.server;

import com.alibaba.fastjson.JSON;
import com.lq.im.codec.body.OfflineNotificationMessageBody;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ImClientDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 8) {
            in.resetReaderIndex();
            return;
        }
        int command = in.readInt();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        OfflineNotificationMessageBody notificationMessageBody = JSON.parseObject(new String(data), OfflineNotificationMessageBody.class);
        notificationMessageBody.setCommand(command);
        out.add(notificationMessageBody);
        in.markReaderIndex();
    }
}
