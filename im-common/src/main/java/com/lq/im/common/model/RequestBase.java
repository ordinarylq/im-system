package com.lq.im.common.model;

import lombok.Data;

@Data
public class RequestBase {
    /**
     * 应用id
     */
    private Integer appId;

    /**
     * 操作人
     */
    private String operator;

    private Integer clientType;

    private String imei;
}
