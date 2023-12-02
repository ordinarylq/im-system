package com.lq.im.codec.body;

import lombok.Data;

import java.io.Serializable;

@Data
public class OfflineNotificationMessageBody<T> implements Serializable {
    private Integer appId;
    private String userId;
    private String receiverId;
    private Integer clientType;
    private String messageId;
    private String imei;
    private Integer command;
    private T data;
}
