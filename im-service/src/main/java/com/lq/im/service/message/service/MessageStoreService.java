package com.lq.im.service.message.service;

import com.alibaba.fastjson.JSONObject;
import com.lq.im.common.constant.Constants;
import com.lq.im.common.enums.user.DelFlagEnum;
import com.lq.im.common.model.message.GroupMessageContent;
import com.lq.im.common.model.message.MessageContent;
import com.lq.im.common.model.message.PeerToPeerMessageBodyDTO;
import com.lq.im.common.model.message.PeerToPeerMessageStoreDTO;
import com.lq.im.service.message.mapper.ImGroupMessageHistoryMapper;
import com.lq.im.service.message.mapper.ImMessageBodyMapper;
import com.lq.im.service.message.mapper.ImMessageHistoryMapper;
import com.lq.im.service.message.model.ImGroupMessageHistoryDAO;
import com.lq.im.service.message.model.ImMessageBodyDAO;
import com.lq.im.service.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MessageStoreService {

    @Resource
    private ImMessageHistoryMapper imMessageHistoryMapper;
    @Resource
    private ImGroupMessageHistoryMapper imGroupMessageHistoryMapper;
    @Resource
    private ImMessageBodyMapper imMessageBodyMapper;
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
        ImMessageBodyDAO messageBody = getMessageBodyFrom(messageContent);
        PeerToPeerMessageStoreDTO messageStoreDTO = new PeerToPeerMessageStoreDTO();
        messageStoreDTO.setMessageContent(messageContent);
        PeerToPeerMessageBodyDTO messageBodyDTO = new PeerToPeerMessageBodyDTO();
        BeanUtils.copyProperties(messageBody, messageBodyDTO);
        messageStoreDTO.setMessageBody(messageBodyDTO);
        messageContent.setMessageKey(messageBody.getMessageKey());
        this.rabbitTemplate.convertAndSend(Constants.MessageQueueConstants.STORE_P2P_MESSAGE, "",
                JSONObject.toJSONString(messageStoreDTO));
    }

    private ImMessageBodyDAO getMessageBodyFrom(MessageContent messageContent) {
        ImMessageBodyDAO messageBodyDAO = new ImMessageBodyDAO();
        messageBodyDAO.setAppId(messageContent.getAppId());
        messageBodyDAO.setMessageKey(this.snowflakeIdWorker.getNextId());
        messageBodyDAO.setMessageBody(messageContent.getMessageData());
        // todo message data encryption
        messageBodyDAO.setSecurityKey("");
        messageBodyDAO.setMessageTime(messageContent.getMessageTime());
        messageBodyDAO.setCreateTime(System.currentTimeMillis());
        messageBodyDAO.setExtra(messageContent.getExtra());
        messageBodyDAO.setDelFlag(DelFlagEnum.NORMAL.getCode());
        return messageBodyDAO;
    }

    /**
     * 读扩散
     */
    @Transactional
    public void storeGroupMessage(GroupMessageContent msgContent) {
        ImMessageBodyDAO messageBody = getMessageBodyFrom(msgContent);
        this.imMessageBodyMapper.insert(messageBody);
        msgContent.setMessageKey(messageBody.getMessageKey());
        ImGroupMessageHistoryDAO groupMessageHistory = getGroupMessageHistoryFrom(msgContent);
        this.imGroupMessageHistoryMapper.insert(groupMessageHistory);
    }

    private ImGroupMessageHistoryDAO getGroupMessageHistoryFrom(GroupMessageContent groupMessageContent) {
        ImGroupMessageHistoryDAO groupMsgHistoryDAO = new ImGroupMessageHistoryDAO();
        BeanUtils.copyProperties(groupMessageContent, groupMsgHistoryDAO);
        groupMsgHistoryDAO.setUserId(groupMessageContent.getUserClient().getUserId());
        groupMsgHistoryDAO.setCreateTime(System.currentTimeMillis());
        return groupMsgHistoryDAO;
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
