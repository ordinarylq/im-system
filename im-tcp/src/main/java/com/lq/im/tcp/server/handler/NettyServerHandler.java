package com.lq.im.tcp.server.handler;

import com.lq.im.codec.proto.Message;
import com.lq.im.codec.proto.MessageHeader;
import com.lq.im.common.constant.Constants;
import com.lq.im.common.enums.gateway.SystemCommand;
import com.lq.im.tcp.mq.producer.MessageProducer;
import com.lq.im.tcp.utils.SessionHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {

    private Integer brokerId;

    public NettyServerHandler(Integer brokerId) {
        this.brokerId = brokerId;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        MessageHeader header = msg.getHeader();
        Integer command = header.getCommand();
        if (command == SystemCommand.LOGIN.getCommand()) {
            SessionHandler.login((NioSocketChannel) ctx.channel(), msg, brokerId);
        } else if (command == SystemCommand.LOGOUT.getCommand()) {
            SessionHandler.logout((NioSocketChannel) ctx.channel());
        } else if (command == SystemCommand.PING.getCommand()) {
            ctx.channel().attr(AttributeKey.valueOf(Constants.LAST_READ_TIME)).set(System.currentTimeMillis());
        } else {
            MessageProducer.sendMessage(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("An error occurred: {}", cause.getMessage());
        log.error("Removing problematic channel");
        SessionHandler.logout((NioSocketChannel) ctx.channel());
    }
}
