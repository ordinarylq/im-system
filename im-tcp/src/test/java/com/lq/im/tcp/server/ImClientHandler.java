package com.lq.im.tcp.server;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ImClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("成功与服务端建立连接！");
        byte[] imei = UUID.randomUUID().toString().getBytes();
        MyMessage message = new MyMessage("liqi", "bot", "Hello, World!");
        byte[] messageData = JSONObject.toJSONString(message).getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < 100; i++) {
            ByteBuf buffer = ctx.alloc().buffer();
            buffer.writeInt(9888)
                    .writeInt(1)
                    .writeInt(4)
                    .writeInt(10000)
                    .writeInt(0x0)
                    .writeInt(imei.length)
                    .writeInt(messageData.length);
            buffer.writeBytes(imei);
            buffer.writeBytes(messageData);
            ctx.writeAndFlush(buffer);
        }
    }
}
