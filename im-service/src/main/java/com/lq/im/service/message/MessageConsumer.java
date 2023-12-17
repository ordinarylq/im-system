package com.lq.im.service.message;

import static com.lq.im.common.constant.Constants.*;

import com.alibaba.fastjson.JSONObject;
import com.lq.im.common.enums.message.MessageCommand;
import com.lq.im.service.message.model.MessageContent;
import com.lq.im.service.message.service.PeerToPeerMessageService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
public class MessageConsumer {

    @Resource
    private PeerToPeerMessageService peerToPeerMessageService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MessageQueueConstants.IM_TO_MESSAGE_SERVICE, durable = "true"),
                    exchange = @Exchange(value = MessageQueueConstants.IM_TO_MESSAGE_SERVICE)
            ),
            concurrency = "1"
    )
    public void consumeMessage(@Payload Message message, Channel channel, @Headers Map<String, Object> headers) throws IOException {
        String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        log.info("Got one message from IM: {}", messageBody);
        try {
            JSONObject jsonObject = JSONObject.parseObject(messageBody);
            Integer command = jsonObject.getInteger("command");
            if (command == MessageCommand.PEER_TO_PEER.getCommand()) {
                MessageContent messageContent = jsonObject.toJavaObject(MessageContent.class);
                this.peerToPeerMessageService.process(messageContent);
            }
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Parse message error: {}", e.getMessage());
            log.error("Message nack: {}", messageBody);
            channel.basicNack(deliveryTag, false, false);
        }

    }
}
