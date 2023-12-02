package com.lq.im.common.enums.friendship;

public enum ApproveFriendRequestStatusEnum {
    AGREE(1),
    REJECT(2)
    ;

    private final int code;

    ApproveFriendRequestStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
