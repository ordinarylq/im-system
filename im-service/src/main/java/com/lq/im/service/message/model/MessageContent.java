package com.lq.im.service.message.model;

import com.lq.im.common.model.UserClientDTO;
import lombok.Data;

@Data
public class MessageContent {

    private String messageId;

    private UserClientDTO userClient;

    private String friendUserId;

    private String messageData;

    private Integer command;

}
