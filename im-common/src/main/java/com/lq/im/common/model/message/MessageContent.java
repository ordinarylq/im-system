package com.lq.im.common.model.message;

import com.lq.im.common.model.UserClientDTO;
import lombok.Data;

@Data
public class MessageContent {

    private Integer appId;

    private String messageId;

    private UserClientDTO userClient;

    private String friendUserId;

    private String messageData;

    private Integer command;

    private Long messageKey;

    private String messageRandom;

    private Long messageTime;

    private String extra;

    private Long sequence;

}
