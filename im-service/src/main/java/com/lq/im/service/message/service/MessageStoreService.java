package com.lq.im.service.message.service;

import com.lq.im.common.enums.user.DelFlagEnum;
import com.lq.im.common.model.message.GroupMessageContent;
import com.lq.im.common.model.message.MessageContent;
import com.lq.im.service.message.mapper.ImGroupMessageHistoryMapper;
import com.lq.im.service.message.mapper.ImMessageBodyMapper;
import com.lq.im.service.message.mapper.ImMessageHistoryMapper;
import com.lq.im.service.message.model.ImGroupMessageHistoryDAO;
import com.lq.im.service.message.model.ImMessageBodyDAO;
import com.lq.im.service.message.model.ImMessageHistoryDAO;
import com.lq.im.service.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 写扩散
     */
    @Transactional
    public void storeP2PMessage(MessageContent messageContent) {
        ImMessageBodyDAO messageBody = getMessageBodyFrom(messageContent);
        messageContent.setMessageKey(messageBody.getMessageKey());
        this.imMessageBodyMapper.insert(messageBody);
        List<ImMessageHistoryDAO> msgHistory = getMessageHistoryFrom(messageContent);
        this.imMessageHistoryMapper.insertBatchSomeColumn(msgHistory);
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

    private List<ImMessageHistoryDAO> getMessageHistoryFrom(MessageContent messageContent) {
        List<ImMessageHistoryDAO> msgHistoryList = new ArrayList<>();
        ImMessageHistoryDAO senderMessageHistory = new ImMessageHistoryDAO();
        BeanUtils.copyProperties(messageContent, senderMessageHistory);
        senderMessageHistory.setUserId(messageContent.getUserClient().getUserId());
        senderMessageHistory.setOwnerId(messageContent.getUserClient().getUserId());
        senderMessageHistory.setCreateTime(System.currentTimeMillis());
        msgHistoryList.add(senderMessageHistory);
        ImMessageHistoryDAO receiverMessageHistory = new ImMessageHistoryDAO();
        BeanUtils.copyProperties(messageContent, receiverMessageHistory);
        receiverMessageHistory.setUserId(messageContent.getUserClient().getUserId());
        receiverMessageHistory.setOwnerId(messageContent.getFriendUserId());
        receiverMessageHistory.setCreateTime(System.currentTimeMillis());
        msgHistoryList.add(receiverMessageHistory);
        return msgHistoryList;
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
