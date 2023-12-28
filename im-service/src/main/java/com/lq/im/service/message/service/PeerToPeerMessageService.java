package com.lq.im.service.message.service;

import com.lq.im.codec.body.ChatMessageAck;
import com.lq.im.codec.body.MessageReceiveServerAckContent;
import com.lq.im.codec.proto.ImServiceMessage;
import com.lq.im.common.ResponseVO;
import com.lq.im.common.constant.Constants;
import com.lq.im.common.enums.message.MessageCommand;
import com.lq.im.common.model.UserClientDTO;
import com.lq.im.common.model.message.MessageContent;
import com.lq.im.common.model.message.MessageReceiveAckContent;
import com.lq.im.service.message.model.req.SendPeerToPeerMessageReq;
import com.lq.im.service.message.model.resp.SendPeerToPeerMessageResp;
import com.lq.im.service.utils.MessageSequenceUtils;
import com.lq.im.service.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class PeerToPeerMessageService {

    @Resource
    private MessageUtils messageUtils;
    @Resource
    private MessageStoreService messageStoreService;
    @Resource(name = "p2pMessageProcessThreadPool")
    private ThreadPoolExecutor msgProcessThreadPool;
    @Resource
    private RedisSequenceService redisSequenceService;

    public void process(MessageContent messageContent) {
        MessageContent messageFromCache = this.messageStoreService.getMessageFromCache(messageContent.getAppId(), messageContent.getMessageId());
        if (messageFromCache != null) {
            // cache hit
            this.msgProcessThreadPool.execute(() -> {
                ack(messageContent, ResponseVO.successResponse());
                forwardMessageToSenderEndpoints(messageFromCache);
                sendMessageToReceiverEndpoints(messageFromCache);
            });
            return;
        }
        // no cache
        String sequenceKey = messageContent.getAppId() + ":" + Constants.RedisConstants.MESSAGE_SEQUENCE + ":"
                + MessageSequenceUtils.getPeerToPeerRedisKey(messageContent.getUserClient().getUserId(),
                messageContent.getFriendUserId());
        Long sequence = this.redisSequenceService.getSequence(sequenceKey);
        messageContent.setSequence(sequence);
        this.msgProcessThreadPool.execute(() -> {
            this.messageStoreService.storeP2PMessage(messageContent);
            ack(messageContent, ResponseVO.successResponse());
            forwardMessageToSenderEndpoints(messageContent);
            sendMessageToReceiverEndpoints(messageContent);
            this.messageStoreService.storeMessageToCache(messageContent);
        });
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
        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId(), messageContent.getSequence());
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
        serviceMessage.setData(messageContent);
        serviceMessage.setReceiverUserId(messageContent.getUserClient().getUserId());
        serviceMessage.setMessageId(messageContent.getMessageId());
        this.messageUtils.sendMessage(MessageCommand.PEER_TO_PEER, serviceMessage, messageContent.getUserClient());
    }

    private void sendMessageToReceiverEndpoints(MessageContent messageContent) {
        ImServiceMessage<Object> serviceMessage = new ImServiceMessage<>();
        BeanUtils.copyProperties(messageContent.getUserClient(), serviceMessage);
        serviceMessage.setCommand(MessageCommand.PEER_TO_PEER.getCommand());
        serviceMessage.setData(messageContent);
        serviceMessage.setReceiverUserId(messageContent.getFriendUserId());
        serviceMessage.setMessageId(messageContent.getMessageId());
        List<UserClientDTO> successList = this.messageUtils.sendMessageToAllDevicesOfOneUser(messageContent.getUserClient().getAppId(),
                messageContent.getFriendUserId(), MessageCommand.PEER_TO_PEER, serviceMessage);
        if (successList.isEmpty()) {
            // 服务端需要发送接受确认ack给发送端
            sendMessageAckFromServer(messageContent);
        }
    }

    private void sendMessageAckFromServer(MessageContent messageContent) {
        MessageReceiveServerAckContent ackContent = new MessageReceiveServerAckContent();
        ackContent.setAppId(messageContent.getAppId());
        ackContent.setUserId(messageContent.getFriendUserId());
        ackContent.setFriendUserId(messageContent.getUserClient().getUserId());
        ackContent.setMessageId(messageContent.getMessageId());
        ackContent.setMessageKey(messageContent.getMessageKey());
        ackContent.setServerSend(true);

        ImServiceMessage<MessageReceiveServerAckContent> serviceMessage = new ImServiceMessage<>();
        serviceMessage.setData(ackContent);
        BeanUtils.copyProperties(messageContent.getUserClient(), serviceMessage);
        serviceMessage.setMessageId(messageContent.getMessageId());
        serviceMessage.setCommand(MessageCommand.MESSAGE_RECEIVE_ACK.getCommand());

        this.messageUtils.sendMessageToOneDevice(MessageCommand.MESSAGE_RECEIVE_ACK, serviceMessage,
                messageContent.getUserClient());
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

    public void receiveMark(MessageReceiveAckContent content) {
        this.messageUtils.sendMessageToAllDevicesOfOneUser(content.getAppId(),
                content.getFriendUserId(), MessageCommand.MESSAGE_RECEIVE_ACK, content);
    }

}
