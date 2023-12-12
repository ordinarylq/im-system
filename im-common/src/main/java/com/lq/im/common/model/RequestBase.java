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
    /**
     * 客户端类型
     */
    private Integer clientType;
    /**
     * 设备唯一编号
     */
    private String imei;
}
