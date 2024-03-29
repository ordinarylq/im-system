package com.lq.im.codec.proto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ImServiceMessage<T> implements Serializable {
    private Integer appId;
    private String userId;
    private String receiverUserId;
    private Integer clientType;
    private String messageId;
    private String imei;
    private Integer command;
    private T data;
}
