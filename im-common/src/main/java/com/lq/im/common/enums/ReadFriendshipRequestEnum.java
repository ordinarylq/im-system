package com.lq.im.common.enums;

/**
 * @ClassName: ReadFriendshipRequestEnum
 * @Author: LiQi
 * @Date: 2023-05-31 14:37
 * @Version: V1.0
 */
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
