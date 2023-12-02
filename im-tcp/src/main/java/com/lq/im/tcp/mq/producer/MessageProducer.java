package com.lq.im.tcp.mq.producer;

import com.alibaba.fastjson.JSONObject;
import com.lq.im.tcp.mq.MQChannelFactory;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Slf4j
public class MessageProducer {

    public static void sendMessage(Object message) {
        // todo 发送消息
        Channel channel;
        String channelName = "";
        try {
            channel = MQChannelFactory.getChannel(channelName);
            channel.basicPublish("", "", null,
                    JSONObject.toJSONString(message).getBytes(StandardCharsets.UTF_8));
        } catch (IOException | TimeoutException e) {
            log.error("An error occurred while sending message.", e);
            throw new RuntimeException(e);
        }
    }
}
