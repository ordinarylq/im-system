package com.lq.im.common.model.message;

import lombok.Data;

@Data
public class SendMessageCheckReq {

    private Integer appId;

    private String userId;

    private String friendUserId;

    private Integer command;

}
