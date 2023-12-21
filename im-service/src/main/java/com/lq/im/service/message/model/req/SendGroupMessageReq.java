package com.lq.im.service.message.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

@Data
public class SendGroupMessageReq extends RequestBase {

    private String groupId;

    private String messageId;

    private String userId;

    private String messageData;

    private String messageRandom;

    private Long messageTime;

    private String extra;

    /**
     * 缺省或者为 0 表示需要计数，
     * 为 1 表示本条消息不需要计数，即右上角图标数字不增加
     */
    private int badgeMode;

    private Long messageLifeTime;
}
