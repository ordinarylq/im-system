package com.lq.im.tcp.server;

import com.alibaba.fastjson.JSONObject;
import com.lq.im.codec.body.OfflineNotificationMessageBody;
import com.lq.im.codec.proto.ImServiceGroupMessage;
import com.lq.im.codec.proto.ImServiceMessage;
import com.lq.im.common.ResponseVO;
import com.lq.im.common.enums.gateway.SystemCommand;
import com.lq.im.common.enums.message.MessageCommand;
import com.lq.im.common.model.UserClientDTO;
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

    private final String userId = "test004";
    private final Integer clientType = 5;
    private final Integer appId = 1000;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("成功与服务端建立连接！");
        imei = UUID.randomUUID().toString().getBytes();
        // login
        String data = "{\"userId\": \"" + userId + "\"}";
        byte[] messageData = data.getBytes(StandardCharsets.UTF_8);
        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeInt(9000)
                .writeInt(1)
                .writeInt(clientType)
                .writeInt(appId)
                .writeInt(0x0)
                .writeInt(imei.length)
                .writeInt(messageData.length);
        buffer.writeBytes(imei);
        buffer.writeBytes(messageData);
        ctx.writeAndFlush(buffer);
        // 启动发送消息的线程
        UserClientDTO userClient = new UserClientDTO(appId, clientType, userId, new String(imei));
        new Thread(new MessageBox(ctx, userClient)).start();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof OfflineNotificationMessageBody) {
            OfflineNotificationMessageBody messageBody = (OfflineNotificationMessageBody) msg;
            if (messageBody.getCommand() == SystemCommand.OFFLINE_NOTIFICATION.getCommand()) {
                log.info("接收到客户端下线通知，退出登录...");
                log.info("与服务器断开连接！");
                String data = "{\"userId\": \"" + userId + "\"}";
                byte[] messageData = data.getBytes(StandardCharsets.UTF_8);
                ByteBuf buffer = ctx.alloc().buffer();
                buffer.writeInt(9003)
                        .writeInt(1)
                        .writeInt(clientType)
                        .writeInt(appId)
                        .writeInt(0x0)
                        .writeInt(imei.length)
                        .writeInt(messageData.length);
                buffer.writeBytes(imei);
                buffer.writeBytes(messageData);
                ChannelFuture f = ctx.writeAndFlush(buffer);
                f.addListener(ChannelFutureListener.CLOSE);
            }
        } else if (msg instanceof ImServiceMessage) {
            ImServiceMessage<?> serviceMessage = (ImServiceMessage<?>) msg;
            if (serviceMessage.getCommand() == MessageCommand.MESSAGE_ACK.getCommand()) {
                ResponseVO<?> resp = ((JSONObject) serviceMessage.getData()).toJavaObject(ResponseVO.class);
                if (resp.isOk()) {
                    System.out.format("%n[消息发送成功]%n");
                } else {
                    System.out.format("%n消息发送失败%n");
                    log.error("detail message: {}", resp);
                }
            } else if (serviceMessage.getCommand() == MessageCommand.PEER_TO_PEER.getCommand()) {
                // 单聊
                JSONObject jsonObject = (JSONObject) JSONObject.toJSON(serviceMessage.getData());
                System.out.format("%n[%s]: [%s]%n", jsonObject.getString("userId"), jsonObject.getString("data"));
                log.info("detail message: {}", serviceMessage.getData());
            } else if (serviceMessage.getCommand() == MessageCommand.PEER_TO_GROUP.getCommand()) {
                // 群聊
                ImServiceGroupMessage<?> groupMessage = (ImServiceGroupMessage<?>) serviceMessage;
                JSONObject jsonObject = (JSONObject) JSONObject.toJSON(groupMessage.getData());
                System.out.format("%n[%s][%s]: [%s]%n", jsonObject.getString("userId"),
                        jsonObject.getString("groupId"), jsonObject.getString("data"));
                log.info("detail message: {}", serviceMessage.getData());
            }
        }
    }
}
