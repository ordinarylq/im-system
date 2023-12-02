package com.lq.im.common.enums.friendship;

public enum ReadFriendshipRequestEnum {
    DID_NOT_READ(0),
    HAS_READ(1)
    ;

    private final Integer code;

    ReadFriendshipRequestEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
