package com.lq.im.tcp.server;

import com.alibaba.fastjson.JSONObject;
import com.lq.im.common.enums.message.MessageCommand;
import com.lq.im.common.model.UserClientDTO;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Slf4j
public class GroupMessageBox implements Runnable{

    private final ChannelHandlerContext ctx;
    private final UserClientDTO userClient;

    public GroupMessageBox(ChannelHandlerContext ctx, UserClientDTO userClient) {
        this.ctx = ctx;
        this.userClient = userClient;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String message;
        ByteBuf buffer;
        while (true) {
            System.out.print(">>>请发送群聊消息(群组id:消息体)>>>");
            message = scanner.nextLine();
            String[] messageArray;
            String groupId;
            String messageData;
            try {
                messageArray = message.split(":");
                groupId = messageArray[0];
                messageData = messageArray[1];
            } catch (Exception e) {
                log.error("Input error! Please write again.");
                continue;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("messageId", "1001");
            jsonObject.put("groupId", groupId);
            jsonObject.put("userClient", userClient);
            jsonObject.put("messageData", messageData);
            byte[] imei = userClient.getImei().getBytes();
            byte[] finalMessage = jsonObject.toString().getBytes(StandardCharsets.UTF_8);
            buffer = ctx.alloc().buffer();
            // command-1000
            buffer.writeInt(MessageCommand.PEER_TO_GROUP.getCommand())
                    // version-1
                    .writeInt(1)
                    // clientType-5
                    .writeInt(userClient.getClientType())
                    // appId-1000
                    .writeInt(userClient.getAppId())
                    // messageType-0
                    .writeInt(0x0)
                    // imei length
                    .writeInt(imei.length)
                    // data length
                    .writeInt(finalMessage.length);
            // imei
            buffer.writeBytes(imei);
            // data
            buffer.writeBytes(finalMessage);
            ctx.writeAndFlush(buffer);
        }
    }
}
