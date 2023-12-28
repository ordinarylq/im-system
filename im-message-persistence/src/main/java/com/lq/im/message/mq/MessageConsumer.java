package com.lq.im.message.mq;

import com.alibaba.fastjson.JSONObject;
import com.lq.im.message.model.GroupMessageStoreDTO;
import com.lq.im.message.model.ImMessageBodyDAO;
import com.lq.im.message.model.PeerToPeerMessageStoreDTO;
import com.lq.im.message.service.StoreMessageService;
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

import static com.lq.im.common.constant.Constants.MessageQueueConstants;

@Slf4j
@Component
public class MessageConsumer {

    @Resource
    private StoreMessageService storeMessageService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MessageQueueConstants.STORE_P2P_MESSAGE, durable = "true"),
                    exchange = @Exchange(value = MessageQueueConstants.STORE_P2P_MESSAGE)
            ),
            concurrency = "1"
    )
    public void consumeMessage(@Payload Message message, Channel channel, @Headers Map<String, Object> headers) throws IOException {
        String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        log.info("Got one message from IM: {}", messageBody);
        try {
            JSONObject jsonObject = JSONObject.parseObject(messageBody);
            PeerToPeerMessageStoreDTO messageStoreDTO = jsonObject.toJavaObject(PeerToPeerMessageStoreDTO.class);
            ImMessageBodyDAO messageBodyDAO = jsonObject.getObject("messageBody", ImMessageBodyDAO.class);
            messageStoreDTO.setMessageBodyDAO(messageBodyDAO);
            this.storeMessageService.storeP2PMessage(messageStoreDTO);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Parse message error: {}", e.getMessage());
            log.error("Message nack: {}", messageBody);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MessageQueueConstants.STORE_GROUP_MESSAGE, durable = "true"),
                    exchange = @Exchange(value = MessageQueueConstants.STORE_GROUP_MESSAGE)
            ),
            concurrency = "1"
    )
    public void consumeGroupMessage(@Payload Message message, Channel channel, @Headers Map<String, Object> headers) throws IOException {
        String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        log.info("Got one group message from IM: {}", messageBody);
        try {
            JSONObject jsonObject = JSONObject.parseObject(messageBody);
            GroupMessageStoreDTO groupMessageStoreDTO = jsonObject.toJavaObject(GroupMessageStoreDTO.class);
            ImMessageBodyDAO messageBodyDAO = jsonObject.getObject("groupMessageBody", ImMessageBodyDAO.class);
            groupMessageStoreDTO.setGroupMessageBodyDAO(messageBodyDAO);
            this.storeMessageService.storeGroupMessage(groupMessageStoreDTO);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Parse group message error: {}", e.getMessage());
            log.error("Group message nack: {}", messageBody);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
