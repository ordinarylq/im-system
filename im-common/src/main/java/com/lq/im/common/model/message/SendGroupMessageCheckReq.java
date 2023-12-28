package com.lq.im.common.model.message;

import lombok.Data;

@Data
public class SendGroupMessageCheckReq {

    private Integer appId;

    private String userId;

    private String groupId;

    private Integer command;

}
