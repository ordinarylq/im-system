package com.lq.im.tcp.server;

import com.lq.im.codec.body.OfflineNotificationMessageBody;
import com.lq.im.common.enums.gateway.SystemCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
public class ImClientHandler extends ChannelInboundHandlerAdapter {
    private byte[] imei;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("成功与服务端建立连接！");
        imei = UUID.randomUUID().toString().getBytes();
//        MyMessage message = new MyMessage("liqi", "bot", "Hello, World!");
//        byte[] messageData = JSONObject.toJSONString(message).getBytes(StandardCharsets.UTF_8);
        String data = "{\"userId\": \"liqi1\"}";
        byte[] messageData = data.getBytes(StandardCharsets.UTF_8);
        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeInt(9000)
                .writeInt(1)
                .writeInt(3)
                .writeInt(10000)
                .writeInt(0x0)
                .writeInt(imei.length)
                .writeInt(messageData.length);
        buffer.writeBytes(imei);
        buffer.writeBytes(messageData);
        ctx.writeAndFlush(buffer);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof OfflineNotificationMessageBody) {
            OfflineNotificationMessageBody messageBody = (OfflineNotificationMessageBody) msg;
            if (messageBody.getCommand() == SystemCommand.OFFLINE_NOTIFICATION.getCommand()) {
                log.info("接收到客户端下线通知，退出登录...");
                log.info("与服务器断开连接！");
                String data = "{\"userId\": \"liqi\"}";
                byte[] messageData = data.getBytes(StandardCharsets.UTF_8);
                ByteBuf buffer = ctx.alloc().buffer();
                buffer.writeInt(9003)
                        .writeInt(1)
                        .writeInt(5)
                        .writeInt(10000)
                        .writeInt(0x0)
                        .writeInt(imei.length)
                        .writeInt(messageData.length);
                buffer.writeBytes(imei);
                buffer.writeBytes(messageData);
                ChannelFuture f = ctx.writeAndFlush(buffer);
                f.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }
}
