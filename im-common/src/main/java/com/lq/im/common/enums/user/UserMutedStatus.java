package com.lq.im.common.enums.user;

public enum UserMutedStatus {

    NOT_MUTED(0),
    MUTED(1)
    ;


    private final int code;

    UserMutedStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
