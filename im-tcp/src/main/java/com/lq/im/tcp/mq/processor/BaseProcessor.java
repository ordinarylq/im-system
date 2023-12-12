package com.lq.im.tcp.mq.processor;

import com.lq.im.codec.proto.ImServiceMessage;
import com.lq.im.common.model.UserClientDTO;
import com.lq.im.tcp.utils.SessionHandler;
import io.netty.channel.socket.nio.NioSocketChannel;

public abstract class BaseProcessor {

    public abstract void beforeProcess();

    public void process(ImServiceMessage<?> message) {
        beforeProcess();
        UserClientDTO userClient =
                new UserClientDTO(message.getAppId(), message.getClientType(), message.getUserId(), message.getImei());
        NioSocketChannel channel = SessionHandler.getChannel(userClient);
        if (channel != null) {
            channel.writeAndFlush(message);
        }
        afterProcess();
    }

    public abstract void afterProcess();

}
