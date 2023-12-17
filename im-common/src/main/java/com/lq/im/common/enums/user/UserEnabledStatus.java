package com.lq.im.common.enums.user;

public enum UserEnabledStatus {

    ENABLED(0),
    DISABLED(1)
    ;


    private final int code;

    UserEnabledStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
