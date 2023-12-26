package com.lq.im.common.model.message;

import lombok.Data;

@Data
public class MessageReceiveAckContent {

    private Integer appId;

    private String userId;

    private String friendUserId;

    private Long messageKey;

    private String messageId;

}
