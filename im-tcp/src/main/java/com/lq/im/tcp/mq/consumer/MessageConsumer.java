package com.lq.im.tcp.mq.consumer;

import static com.lq.im.common.constant.Constants.MessageQueueConstants.*;
import com.lq.im.tcp.mq.MQChannelFactory;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessageConsumer {

    private static Integer brokerId;
    private static void startReceiveMessage() {
        try {
            String channelName = MESSAGE_SERVICE_TO_IM + brokerId;
            String queueName = channelName;
            Channel channel = MQChannelFactory.getChannel(channelName);
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, MESSAGE_SERVICE_TO_IM, String.valueOf(brokerId));
            channel.basicConsume(queueName, false, (consumerTag, message) -> {
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

    public static void init(Integer brokerId) {
        MessageConsumer.brokerId = brokerId;
        startReceiveMessage();
    }
}
