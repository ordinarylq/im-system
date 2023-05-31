package com.lq.im.common.enums;

/**
 * @ClassName: AddFriendshipEnum
 * @Author: LiQi
 * @Date: 2023-04-28 8:18
 * @Version: V1.0
 * @Description:
 */
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
