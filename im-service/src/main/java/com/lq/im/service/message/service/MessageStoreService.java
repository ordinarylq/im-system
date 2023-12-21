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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

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
}
