package com.lq.im.service.message.service;

import com.lq.im.codec.body.ChatMessageAck;
import com.lq.im.codec.proto.ImServiceGroupMessage;
import com.lq.im.common.ResponseVO;
import com.lq.im.common.constant.Constants;
import com.lq.im.common.enums.group.GroupMemberRoleEnum;
import com.lq.im.common.enums.message.MessageCommand;
import com.lq.im.common.model.UserClientDTO;
import com.lq.im.common.model.message.GroupMessageContent;
import com.lq.im.service.group.model.req.ImGroupMemberDTO;
import com.lq.im.service.group.service.ImGroupMemberService;
import com.lq.im.service.message.model.req.SendGroupMessageReq;
import com.lq.im.service.message.model.resp.SendGroupMessageResp;
import com.lq.im.service.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class GroupMessageService {
    @Resource
    private MessageCheckService messageCheckService;
    @Resource
    private MessageUtils messageUtils;
    @Resource
    private ImGroupMemberService imGroupMemberService;
    @Resource
    private MessageStoreService messageStoreService;
    @Resource(name = "groupMessageProcessThreadPool")
    private ThreadPoolExecutor groupMsgProcessThreadPool;
    @Resource
    private RedisSequenceService redisSequenceService;


    public void process(GroupMessageContent groupMessageContent) {
        GroupMessageContent messageFromCache = this.messageStoreService.getMessageFromCache(
                groupMessageContent.getAppId(), groupMessageContent.getMessageId(), GroupMessageContent.class);
        if (messageFromCache != null) {
            // cache hit
            this.groupMsgProcessThreadPool.execute(() -> {
                ack(groupMessageContent, ResponseVO.successResponse());
                forwardMessageToSenderEndpoints(messageFromCache);
                sendMessageToReceiverEndpoints(messageFromCache);
            });
            return;
        }
        // no cache
        String sequenceKey = groupMessageContent.getAppId() + ":" + Constants.RedisConstants.GROUP_MESSAGE_SEQUENCE
                + groupMessageContent.getGroupId();
        Long sequence = this.redisSequenceService.getSequence(sequenceKey);
        groupMessageContent.setSequence(sequence);
        this.groupMsgProcessThreadPool.execute(() -> {
            this.messageStoreService.storeGroupMessage(groupMessageContent);
            ack(groupMessageContent, ResponseVO.successResponse());
            forwardMessageToSenderEndpoints(groupMessageContent);
            sendMessageToReceiverEndpoints(groupMessageContent);
            this.messageStoreService.storeMessageToCache(groupMessageContent.getAppId(),
                    groupMessageContent.getMessageId(), groupMessageContent);
        });
    }

    /**
     * 向发送方响应ack
     */
    private void ack(GroupMessageContent groupMessageContent, ResponseVO responseVO) {
        ImServiceGroupMessage<ResponseVO<?>> serviceMessage = new ImServiceGroupMessage<>();
        serviceMessage.setData(responseVO);
        BeanUtils.copyProperties(groupMessageContent.getUserClient(), serviceMessage);
        serviceMessage.setMessageId(groupMessageContent.getMessageId());
        serviceMessage.setCommand(MessageCommand.GROUP_MESSAGE_ACK.getCommand());
        serviceMessage.setGroupId(groupMessageContent.getGroupId());
        log.info("msg ack, msgId={}, checkResult={}", groupMessageContent.getMessageId(), serviceMessage);
        ChatMessageAck chatMessageAck = new ChatMessageAck(groupMessageContent.getMessageId());
        responseVO.setData(chatMessageAck);
        this.messageUtils.sendMessageToOneDevice(MessageCommand.GROUP_MESSAGE_ACK, serviceMessage,
                groupMessageContent.getUserClient());
    }

    /**
     * 同步消息至发送方的其他端
     */
    private void forwardMessageToSenderEndpoints(GroupMessageContent groupMessageContent) {
        ImServiceGroupMessage<Object> serviceMessage = new ImServiceGroupMessage<>();
        BeanUtils.copyProperties(groupMessageContent.getUserClient(), serviceMessage);
        serviceMessage.setCommand(MessageCommand.PEER_TO_GROUP.getCommand());
        serviceMessage.setData(groupMessageContent.getMessageData());
        serviceMessage.setReceiverUserId(groupMessageContent.getUserClient().getUserId());
        serviceMessage.setGroupId(groupMessageContent.getGroupId());
        serviceMessage.setMessageId(groupMessageContent.getMessageId());
        this.messageUtils.sendMessage(MessageCommand.PEER_TO_GROUP, serviceMessage, groupMessageContent.getUserClient());
    }

    private void sendMessageToReceiverEndpoints(GroupMessageContent groupMessageContent) {
        ResponseVO<List<ImGroupMemberDTO>> groupMemberListResp = this.imGroupMemberService.getGroupMemberList(groupMessageContent.getUserClient().getAppId(), groupMessageContent.getGroupId());
        if (!groupMemberListResp.isOk()) {
            log.error("An error occurred while sending message: {}", groupMemberListResp.getMsg());
            return;
        }
        String senderUserId = groupMessageContent.getUserClient().getUserId();
        groupMemberListResp.getData().stream().filter(groupMemberDTO ->
                groupMemberDTO.getMemberRole() != GroupMemberRoleEnum.LEAVE.getCode()
                        && !Objects.equals(groupMemberDTO.getMemberId(), senderUserId))
                .map(ImGroupMemberDTO::getMemberId).forEach(memberId -> {
            ImServiceGroupMessage<Object> serviceMessage = new ImServiceGroupMessage<>();
            BeanUtils.copyProperties(groupMessageContent.getUserClient(), serviceMessage);
            serviceMessage.setCommand(MessageCommand.PEER_TO_GROUP.getCommand());
            serviceMessage.setData(groupMessageContent.getMessageData());
            serviceMessage.setReceiverUserId(memberId);
            serviceMessage.setGroupId(groupMessageContent.getGroupId());
            serviceMessage.setMessageId(groupMessageContent.getMessageId());
            this.messageUtils.sendMessageToAllDevicesOfOneUser(groupMessageContent.getUserClient().getAppId(),
                    memberId, MessageCommand.PEER_TO_GROUP, serviceMessage);
        });
    }

    public SendGroupMessageResp send(SendGroupMessageReq req) {
        SendGroupMessageResp resp = new SendGroupMessageResp();
        GroupMessageContent groupMessageContent = new GroupMessageContent();
        BeanUtils.copyProperties(req, groupMessageContent);
        UserClientDTO userClient = new UserClientDTO();
        userClient.setAppId(req.getAppId());
        userClient.setUserId(req.getUserId());
        groupMessageContent.setUserClient(userClient);
        this.messageStoreService.storeGroupMessage(groupMessageContent);
        forwardMessageToSenderEndpoints(groupMessageContent);
        sendMessageToReceiverEndpoints(groupMessageContent);
        resp.setMessageKey(groupMessageContent.getMessageKey());
        resp.setMessageTime(groupMessageContent.getMessageTime());
        return resp;
    }
}
