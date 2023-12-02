package com.lq.im.common.enums.friendship;

public enum FriendshipCheckEnum {
    SINGLE(1),
    BOTH(2)
    ;

    private final Integer type;

    FriendshipCheckEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
