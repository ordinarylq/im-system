package com.lq.im.common.enums;

/**
 * @ClassName: DelFlagEnum
 * @Author: LiQi
 * @Date: 2023-04-11 15:48
 * @Version: V1.0
 */
public enum DelFlagEnum {
    NORMAL(0),
    DELETED(1);

    /**
     * 0-未删除 1-已删除
     */
    private final int code;

    DelFlagEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
