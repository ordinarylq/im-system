package com.lq.im.tcp.mq.subscribe;

import static com.lq.im.common.constant.Constants.MessageQueueConstants.*;
import com.lq.im.tcp.mq.MQChannelFactory;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessageConsumer {
    private static void startReceiveMessage() {
        try {
            Channel channel = MQChannelFactory.getChannel(MESSAGE_SERVICE_TO_IM);
            channel.queueDeclare(MESSAGE_SERVICE_TO_IM, true, false, false, null);
            channel.queueBind(MESSAGE_SERVICE_TO_IM, MESSAGE_SERVICE_TO_IM, "");
            channel.basicConsume(MESSAGE_SERVICE_TO_IM, false, (consumerTag, message) -> {
                // todo 消费者处理消息
                System.out.println(new String(message.getBody()));
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            }, consumerTag -> {});
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public static void init() {
        startReceiveMessage();
    }
}
