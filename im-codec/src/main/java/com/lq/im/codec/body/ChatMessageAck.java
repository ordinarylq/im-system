package com.lq.im.codec.body;

import lombok.Data;

@Data
public class ChatMessageAck {
    private String messageId;

    public ChatMessageAck(String messageId) {
        this.messageId = messageId;
    }
}
