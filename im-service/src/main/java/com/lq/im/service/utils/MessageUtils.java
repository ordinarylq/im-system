package com.lq.im.service.utils;

import com.alibaba.fastjson.JSONObject;
import com.lq.im.codec.proto.ImServiceMessage;
import com.lq.im.common.enums.command.Command;
import com.lq.im.common.model.UserClientDTO;
import com.lq.im.common.model.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.lq.im.common.constant.Constants.MessageQueueConstants.MESSAGE_SERVICE_TO_IM;

@Slf4j
@Component
public class MessageUtils {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private UserSessionUtils userSessionUtils;

    public boolean sendMessage(UserSession session, Object msg) {
        String exchangeName = MESSAGE_SERVICE_TO_IM;
        try {
            this.rabbitTemplate.convertAndSend(exchangeName, String.valueOf(session.getBrokerId()), JSONObject.toJSONString(msg));
        } catch (AmqpException e) {
            log.error("Send MQ message error: {}", e.getMessage());
            return false;
        }
        return true;
    }

    public List<UserClientDTO> sendMessageToAllDevicesOfOneUser(Integer appId, String receiverUserId, Command command, Object data) {
        List<UserSession> sessionList = this.userSessionUtils.getUserSession(appId, receiverUserId);
        List<UserClientDTO> successUserClientList = new ArrayList<>();
        for (UserSession userSession : sessionList) {
            boolean result = sendMessage(userSession, new ImServiceMessage<>(
                    userSession.getAppId(), userSession.getUserId(), receiverUserId, userSession.getClientType(),
                    null, userSession.getImei(), command.getCommand(), data
            ));
            if (result) {
                successUserClientList.add(new UserClientDTO(userSession.getAppId(), userSession.getClientType(), userSession.getUserId(),
                        userSession.getImei()));
            }
        }
        return successUserClientList;
    }

    public void sendMessage(Command command, Object data, UserClientDTO userClient) {
        if (userClient.getClientType() == null || StringUtils.isEmpty(userClient.getImei())) {
            sendMessageToAllDevicesOfOneUser(userClient.getAppId(), userClient.getUserId(), command, data);
        } else {
            sendMessageExceptOneDevice(command, data, userClient, userClient.getUserId());
        }
    }

    public boolean sendMessageToOneDevice(Command command, Object data, UserClientDTO userClient) {
        UserSession userSession = this.userSessionUtils.getUserSession(userClient);
        return sendMessage(userSession, data);
    }

    public void sendMessageExceptOneDevice(Command command, Object data, UserClientDTO userClient, String receiverUserId) {
        List<UserSession> sessionList = this.userSessionUtils.getUserSession(userClient.getAppId(), userClient.getUserId());
        for (UserSession userSession : sessionList) {
            if (isOtherUserClientSession(userSession, userClient)) {
                sendMessage(userSession, new ImServiceMessage<>(
                        userSession.getAppId(), userSession.getUserId(), receiverUserId, userSession.getClientType(),
                        null, userSession.getImei(), command.getCommand(), data
                ));
            }
        }
    }

    private boolean isOtherUserClientSession(UserSession userSession, UserClientDTO userClient) {
        return !Objects.equals(userSession.getClientType(), userClient.getClientType()) || !Objects.equals(userSession.getImei(),
                userClient.getImei());
    }
}
