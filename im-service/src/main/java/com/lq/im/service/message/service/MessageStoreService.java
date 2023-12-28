package com.lq.im.service.message.service;

import com.alibaba.fastjson.JSONObject;
import com.lq.im.common.constant.Constants;
import com.lq.im.common.enums.user.DelFlagEnum;
import com.lq.im.common.model.message.*;
import com.lq.im.service.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MessageStoreService {

    @Resource
    private SnowflakeIdWorker snowflakeIdWorker;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 写扩散
     */
    @Transactional
    public void storeP2PMessage(MessageContent messageContent) {
        MessageBodyDTO messageBody = getMessageBodyFrom(messageContent);
        PeerToPeerMessageStoreDTO messageStoreDTO = new PeerToPeerMessageStoreDTO();
        messageStoreDTO.setMessageContent(messageContent);
        messageStoreDTO.setMessageBody(messageBody);
        messageContent.setMessageKey(messageBody.getMessageKey());
        this.rabbitTemplate.convertAndSend(Constants.MessageQueueConstants.STORE_P2P_MESSAGE, "",
                JSONObject.toJSONString(messageStoreDTO));
    }

    private MessageBodyDTO getMessageBodyFrom(MessageContent messageContent) {
        MessageBodyDTO messageBody = new MessageBodyDTO();
        messageBody.setAppId(messageContent.getAppId());
        messageBody.setMessageKey(this.snowflakeIdWorker.getNextId());
        messageBody.setMessageBody(messageContent.getMessageData());
        // todo message data encryption
        messageBody.setSecurityKey("");
        messageBody.setMessageTime(messageContent.getMessageTime());
        messageBody.setCreateTime(System.currentTimeMillis());
        messageBody.setExtra(messageContent.getExtra());
        messageBody.setDelFlag(DelFlagEnum.NORMAL.getCode());
        return messageBody;
    }

    /**
     * 读扩散
     */
    @Transactional
    public void storeGroupMessage(GroupMessageContent msgContent) {
        MessageBodyDTO messageBody = getMessageBodyFrom(msgContent);
        GroupMessageStoreDTO groupMessageStoreDTO = new GroupMessageStoreDTO();
        groupMessageStoreDTO.setGroupMessageContent(msgContent);
        groupMessageStoreDTO.setGroupMessageBody(messageBody);
        msgContent.setMessageKey(messageBody.getMessageKey());
        this.rabbitTemplate.convertAndSend(Constants.MessageQueueConstants.STORE_GROUP_MESSAGE, "",
                JSONObject.toJSONString(groupMessageStoreDTO));
    }

    /**
     * 存储消息(包括messageId)到Redis
     * 消息发送方可能会重复发送消息，使用缓存来去重
     */
    public void storeMessageToCache(MessageContent messageContent) {
        String key = getMessageCacheKey(messageContent.getAppId(), messageContent.getMessageId());
        this.stringRedisTemplate.opsForValue().set(key, JSONObject.toJSONString(messageContent),
                300, TimeUnit.SECONDS);
    }

    /**
     * 从缓存中获取消息
     */
    public MessageContent getMessageFromCache(Integer appId, String messageId) {
        String key = getMessageCacheKey(appId, messageId);
        String message = this.stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(message)) {
            return null;
        }
        return JSONObject.parseObject(message, MessageContent.class);
    }

    private String getMessageCacheKey(Integer appId, String messageId) {
        return appId + ":" + Constants.RedisConstants.CACHE_MESSAGE + ":" + messageId;
    }
}
