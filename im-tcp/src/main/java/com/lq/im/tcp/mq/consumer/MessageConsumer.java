package com.lq.im.tcp.mq.consumer;

import static com.lq.im.common.constant.Constants.MessageQueueConstants.*;

import com.alibaba.fastjson.JSONObject;
import com.lq.im.codec.proto.ImServiceMessage;
import com.lq.im.tcp.mq.MQChannelFactory;
import com.lq.im.tcp.mq.processor.MessageProcessFactory;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
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
                try {
                    ImServiceMessage<?> imServiceMessage =
                            JSONObject.parseObject(new String(message.getBody()), ImServiceMessage.class);
                    log.info("TCP Service getting message: {}", new String(message.getBody()));
                    MessageProcessFactory.getMessageProcessor(imServiceMessage.getCommand()).process(imServiceMessage);
                    channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
                } catch (IOException e) {
                    log.error("An error occurred while consuming message: {}", e.getMessage());
                    channel.basicNack(message.getEnvelope().getDeliveryTag(), false, false);
                }
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
