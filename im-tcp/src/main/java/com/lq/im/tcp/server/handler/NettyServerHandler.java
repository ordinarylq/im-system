package com.lq.im.tcp.server.handler;

import com.lq.im.codec.proto.Message;
import com.lq.im.codec.proto.MessageHeader;
import com.lq.im.common.constant.Constants;
import com.lq.im.common.enums.SystemCommand;
import com.lq.im.tcp.utils.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        MessageHeader header = msg.getHeader();
        Integer command = header.getCommand();
        if (command == SystemCommand.LOGIN.getCommand()) {
            SessionSocketHolder.login((NioSocketChannel) ctx.channel(), msg);
        } else if (command == SystemCommand.LOGOUT.getCommand()) {
            SessionSocketHolder.logout((NioSocketChannel) ctx.channel());
        } else if (command == SystemCommand.PING.getCommand()) {
            ctx.channel().attr(AttributeKey.valueOf(Constants.LAST_READ_TIME)).set(System.currentTimeMillis());
        }
    }
}
