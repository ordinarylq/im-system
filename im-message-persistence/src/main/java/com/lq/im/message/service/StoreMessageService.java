package com.lq.im.message.service;

import com.lq.im.common.model.message.GroupMessageContent;
import com.lq.im.common.model.message.MessageContent;
import com.lq.im.message.mapper.ImGroupMessageHistoryMapper;
import com.lq.im.message.mapper.ImMessageBodyMapper;
import com.lq.im.message.mapper.ImMessageHistoryMapper;
import com.lq.im.message.model.GroupMessageStoreDTO;
import com.lq.im.message.model.ImGroupMessageHistoryDAO;
import com.lq.im.message.model.ImMessageHistoryDAO;
import com.lq.im.message.model.PeerToPeerMessageStoreDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class StoreMessageService {

    @Resource
    private ImMessageHistoryMapper imMessageHistoryMapper;
    @Resource
    private ImMessageBodyMapper imMessageBodyMapper;
    @Resource
    private ImGroupMessageHistoryMapper imGroupMessageHistoryMapper;

    @Transactional
    public void storeP2PMessage(PeerToPeerMessageStoreDTO messageStoreDTO) {
        this.imMessageBodyMapper.insert(messageStoreDTO.getMessageBodyDAO());
        List<ImMessageHistoryDAO> msgHistory = getMessageHistoryFrom(messageStoreDTO.getMessageContent());
        this.imMessageHistoryMapper.insertBatchSomeColumn(msgHistory);
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

    @Transactional
    public void storeGroupMessage(GroupMessageStoreDTO groupMessageStoreDTO) {
        this.imMessageBodyMapper.insert(groupMessageStoreDTO.getGroupMessageBodyDAO());
        ImGroupMessageHistoryDAO groupMessageHistory =
                getGroupMessageHistoryFrom(groupMessageStoreDTO.getGroupMessageContent());
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
