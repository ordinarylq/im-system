package com.lq.im.tcp.server;

import com.alibaba.fastjson.JSON;
import com.lq.im.codec.body.OfflineNotificationMessageBody;
import com.lq.im.codec.proto.ImServiceMessage;
import com.lq.im.common.enums.gateway.SystemCommand;
import com.lq.im.common.enums.message.MessageCommand;
import com.lq.im.common.enums.user.UserCommand;
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
        if (command == SystemCommand.OFFLINE_NOTIFICATION.getCommand()) {
            OfflineNotificationMessageBody<?> notificationMessageBody = JSON.parseObject(new String(data), OfflineNotificationMessageBody.class);
            notificationMessageBody.setCommand(command);
            out.add(notificationMessageBody);
        } else if (command == UserCommand.USER_INFO_MODIFIED.getCommand()) {
            ImServiceMessage<?> message = JSON.parseObject(new String(data), ImServiceMessage.class);
            out.add(message);
        } else if (command == MessageCommand.MESSAGE_ACK.getCommand()) {
            ImServiceMessage<?> message = JSON.parseObject(new String(data), ImServiceMessage.class);
            out.add(message);
        } else if (command == MessageCommand.PEER_TO_PEER.getCommand()) {
            ImServiceMessage<?> message = JSON.parseObject(new String(data), ImServiceMessage.class);
            out.add(message);
        }
        in.markReaderIndex();
    }
}
