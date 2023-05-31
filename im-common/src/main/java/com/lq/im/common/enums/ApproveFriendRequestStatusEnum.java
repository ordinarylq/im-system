package com.lq.im.common.enums;

/**
 * @ClassName: ApproveFriendRequestStatusEnum
 * @Author: LiQi
 * @Date: 2023-04-28 13:29
 * @Version: V1.0
 */
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
