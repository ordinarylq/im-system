package com.lq.im.common.enums;

public enum ImConnecStatusEnum {
    ONLINE_STATUS(1),
    OFFLINE_STATUS(2),
    ;

    private final int code;

    ImConnecStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
