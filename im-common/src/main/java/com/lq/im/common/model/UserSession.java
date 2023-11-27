package com.lq.im.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 客户端类型
     */
    private Integer clientType;

    /**
     * SDK版本号
     */
    private Integer version;

    /**
     * 连接状态 1-在线 2-离线
     */
    private Integer connectStatus;
}
