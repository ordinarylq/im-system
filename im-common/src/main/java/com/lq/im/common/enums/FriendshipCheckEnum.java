package com.lq.im.common.enums;

/**
 * @ClassName: FriendshipCheckEnum
 * @Author: LiQi
 * @Date: 2023-04-18 10:11
 * @Version: V1.0
 */
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
