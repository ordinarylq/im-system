package com.lq.im.tcp.server.handler;

import com.lq.im.common.constant.Constants;
import com.lq.im.tcp.utils.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
    private Long timeout;

    public HeartBeatHandler(Long timeout) {
        this.timeout = timeout;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()) {
                case READER_IDLE:
                    log.info("读空闲");
                    break;
                case WRITER_IDLE:
                    log.info("写空闲");
                    break;
                case ALL_IDLE:
                    Long lastReadTime = (Long) ctx.channel().attr(AttributeKey.valueOf(Constants.LAST_READ_TIME)).get();
                    long currentTime = System.currentTimeMillis();
                    if (lastReadTime != null && currentTime - lastReadTime > timeout) {
                        SessionSocketHolder.offline((NioSocketChannel) ctx.channel());
                    }
                    break;
                default:
                    /*ignore*/
            }
        }
    }
}
