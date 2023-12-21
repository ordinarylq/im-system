package com.lq.im.service.message.service;

import com.lq.im.codec.body.ChatMessageAck;
import com.lq.im.codec.proto.ImServiceMessage;
import com.lq.im.common.ResponseVO;
import com.lq.im.common.enums.message.MessageCommand;
import com.lq.im.common.model.UserClientDTO;
import com.lq.im.common.model.message.MessageContent;
import com.lq.im.service.message.model.req.SendPeerToPeerMessageReq;
import com.lq.im.service.message.model.resp.SendPeerToPeerMessageResp;
import com.lq.im.service.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class PeerToPeerMessageService {

    @Resource
    private MessageCheckService messageCheckService;
    @Resource
    private MessageUtils messageUtils;
    @Resource
    private MessageStoreService messageStoreService;

    public void process(MessageContent messageContent) {
        Integer appId = messageContent.getUserClient().getAppId();
        String userId = messageContent.getUserClient().getUserId();
        String friendUserId = messageContent.getFriendUserId();
        ResponseVO<?> responseVO = this.messageCheckService.checkUserAndFriendship(appId, userId, friendUserId);
        if (!responseVO.isOk()) {
            ack(messageContent, responseVO);
            return;
        }
        this.messageStoreService.storeP2PMessage(messageContent);
        ack(messageContent, responseVO);
        forwardMessageToSenderEndpoints(messageContent);
        sendMessageToReceiverEndpoints(messageContent);
    }

    /**
     * 向发送方响应ack
     */
    private void ack(MessageContent messageContent, ResponseVO responseVO) {
        ImServiceMessage<ResponseVO<?>> serviceMessage = new ImServiceMessage<>();
        serviceMessage.setData(responseVO);
        BeanUtils.copyProperties(messageContent.getUserClient(), serviceMessage);
        serviceMessage.setMessageId(messageContent.getMessageId());
        serviceMessage.setCommand(MessageCommand.MESSAGE_ACK.getCommand());
        log.info("msg ack, msgId={}, checkResult={}", messageContent.getMessageId(), serviceMessage);
        // 1. 建立响应对象
        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId());
        responseVO.setData(chatMessageAck);
        // 2. 发给消息发送方
        this.messageUtils.sendMessageToOneDevice(MessageCommand.MESSAGE_ACK, serviceMessage,
                messageContent.getUserClient());
    }

    /**
     * 同步消息至发送方的其他端
     */
    private void forwardMessageToSenderEndpoints(MessageContent messageContent) {
        ImServiceMessage<Object> serviceMessage = new ImServiceMessage<>();
        BeanUtils.copyProperties(messageContent.getUserClient(), serviceMessage);
        serviceMessage.setCommand(MessageCommand.PEER_TO_PEER.getCommand());
        serviceMessage.setData(messageContent.getMessageData());
        serviceMessage.setReceiverUserId(messageContent.getUserClient().getUserId());
        serviceMessage.setMessageId(messageContent.getMessageId());
        this.messageUtils.sendMessage(MessageCommand.PEER_TO_PEER, serviceMessage, messageContent.getUserClient());
    }

    private void sendMessageToReceiverEndpoints(MessageContent messageContent) {
        ImServiceMessage<Object> serviceMessage = new ImServiceMessage<>();
        BeanUtils.copyProperties(messageContent.getUserClient(), serviceMessage);
        serviceMessage.setCommand(MessageCommand.PEER_TO_PEER.getCommand());
        serviceMessage.setData(messageContent.getMessageData());
        serviceMessage.setReceiverUserId(messageContent.getFriendUserId());
        serviceMessage.setMessageId(messageContent.getMessageId());
        this.messageUtils.sendMessageToAllDevicesOfOneUser(messageContent.getUserClient().getAppId(),
                messageContent.getFriendUserId(), MessageCommand.PEER_TO_PEER, serviceMessage);
    }

    public SendPeerToPeerMessageResp send(SendPeerToPeerMessageReq req) {
        SendPeerToPeerMessageResp resp = new SendPeerToPeerMessageResp();
        MessageContent messageContent = new MessageContent();
        UserClientDTO userClient = new UserClientDTO();
        userClient.setAppId(req.getAppId());
        userClient.setUserId(req.getUserId());
        BeanUtils.copyProperties(req, messageContent);
        messageContent.setUserClient(userClient);
        this.messageStoreService.storeP2PMessage(messageContent);
        resp.setMessageKey(messageContent.getMessageKey());
        resp.setMessageTime(messageContent.getMessageTime());
        forwardMessageToSenderEndpoints(messageContent);
        sendMessageToReceiverEndpoints(messageContent);
        return resp;
    }
}
