package com.lq.im.tcp.mq.producer;

import com.alibaba.fastjson.JSONObject;
import com.lq.im.codec.proto.Message;
import com.lq.im.codec.proto.MessageHeader;
import com.lq.im.common.constant.Constants;
import com.lq.im.tcp.mq.MQChannelFactory;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Slf4j
public class MessageProducer {

    public static void sendMessage(Message message) {
        Channel channel;
        String channelName = Constants.MessageQueueConstants.IM_TO_MESSAGE_SERVICE;
        MessageHeader messageHeader = message.getHeader();
        try {
            channel = MQChannelFactory.getChannel(channelName);
            JSONObject messageBody = (JSONObject) JSONObject.toJSON(message.getBody());
            messageBody.put("command", messageHeader.getCommand());
            channel.basicPublish(channelName, "", null,
                    JSONObject.toJSONString(messageBody).getBytes(StandardCharsets.UTF_8));
        } catch (IOException | TimeoutException e) {
            log.error("An error occurred while sending message.", e);
            throw new RuntimeException(e);
        }
    }
}
