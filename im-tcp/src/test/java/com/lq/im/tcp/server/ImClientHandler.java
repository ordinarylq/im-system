package com.lq.im.tcp.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
public class ImClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("成功与服务端建立连接！");
        byte[] imei = UUID.randomUUID().toString().getBytes();
//        MyMessage message = new MyMessage("liqi", "bot", "Hello, World!");
//        byte[] messageData = JSONObject.toJSONString(message).getBytes(StandardCharsets.UTF_8);
        String data = "{\"userId\": \"liqi1\"}";
        byte[] messageData = data.getBytes(StandardCharsets.UTF_8);
        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeInt(9000)
                .writeInt(1)
                .writeInt(4)
                .writeInt(10000)
                .writeInt(0x0)
                .writeInt(imei.length)
                .writeInt(messageData.length);
        buffer.writeBytes(imei);
        buffer.writeBytes(messageData);
        ctx.writeAndFlush(buffer);

        Thread.sleep(100000);
        log.info("测试超时，断开连接。。。。");
//        log.info("与服务器断开连接！");
//        data = "{\"userId\": \"liqi\"}";
//        messageData = data.getBytes(StandardCharsets.UTF_8);
//        buffer = ctx.alloc().buffer();
//        buffer.writeInt(9003)
//                .writeInt(1)
//                .writeInt(4)
//                .writeInt(10000)
//                .writeInt(0x0)
//                .writeInt(imei.length)
//                .writeInt(messageData.length);
//        buffer.writeBytes(imei);
//        buffer.writeBytes(messageData);
//        ctx.writeAndFlush(buffer);
    }
}
