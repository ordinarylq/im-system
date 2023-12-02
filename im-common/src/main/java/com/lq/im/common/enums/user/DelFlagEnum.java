package com.lq.im.common.enums.user;

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
