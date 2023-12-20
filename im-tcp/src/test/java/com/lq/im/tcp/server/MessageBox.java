package com.lq.im.tcp.server;

import com.alibaba.fastjson.JSONObject;
import com.lq.im.common.enums.message.MessageCommand;
import com.lq.im.common.model.UserClientDTO;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

@Slf4j
public class MessageBox implements Runnable{

    private ChannelHandlerContext ctx;
    private UserClientDTO userClient;

    public MessageBox(ChannelHandlerContext ctx, UserClientDTO userClient) {
        this.ctx = ctx;
        this.userClient = userClient;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String message;
        ByteBuf buffer;
        while (true) {
            System.out.print(">>>请发送单聊消息(接收方:消息体)>>>");
            message = scanner.nextLine();
            String[] messageArray;
            String friendUserId;
            String messageData;
            try {
                messageArray = message.split(":");
                friendUserId = messageArray[0];
                messageData = messageArray[1];
            } catch (Exception e) {
                log.error("Input error! Please write again.");
                continue;
            }
            byte[] imei = userClient.getImei().getBytes();
            byte[] finalMessage = getMsgData(friendUserId, messageData);
            buffer = ctx.alloc().buffer();
            // command-1000
            buffer.writeInt(MessageCommand.PEER_TO_PEER.getCommand())
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

    private byte[] getMsgData(String friendUserId, String messageData) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appId", userClient.getAppId());
        jsonObject.put("messageId", "1001");
        jsonObject.put("userClient", userClient);
        jsonObject.put("friendUserId", friendUserId);
        jsonObject.put("messageData", messageData);
        jsonObject.put("messageRandom", getRandom());
        jsonObject.put("messageTime", System.currentTimeMillis());
        return jsonObject.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String getRandom() {
        Random random = new Random(System.currentTimeMillis());
        return String.valueOf(random.nextLong());
    }
}
