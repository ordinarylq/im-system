package com.lq.im.common.model;

import lombok.Data;

/**
 * @ClassName: RequestBase
 * @Author: LiQi
 * @Date: 2023-04-11 14:16
 * @Version: V1.0
 * @Description:
 */
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
}
