package com.lq.im.codec.body;

import lombok.Data;

@Data
public class MessageReceiveServerAckContent {

    private Integer appId;

    private String userId;

    private String friendUserId;

    private Long messageKey;

    private String messageId;

    private boolean serverSend;
}
