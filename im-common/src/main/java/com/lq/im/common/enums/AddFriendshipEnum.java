package com.lq.im.common.enums;

public enum AddFriendshipEnum {
    NO_NEED_TO_CONFIRM(1),
    NEED_TO_CONFIRM(2)
    ;

    private final Integer code;

    AddFriendshipEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
