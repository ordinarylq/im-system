package com.lq.im.common.enums;

import com.lq.im.common.exception.ApplicationExceptionEnum;

/**
 * @ClassName: FriendshipStatusEnum
 * @Author: LiQi
 * @Date: 2023-04-13 16:12
 * @Version: V1.0
 */
public enum FriendshipStatusEnum {
    /**
     * 好友状态 1-正常 2-删除 0-未添加
     */
    FRIEND_STATUS_NO_FRIEND(0),

    FRIEND_STATUS_NORMAL(1),

    FRIEND_STATUS_DELETE(2),

    /**
     * 是否拉黑好友 1-正常 2-已拉黑
     */
    BLACK_STATUS_NORMAL(1),

    BLACK_STATUS_BLACKED(2),
    ;


    private final int code;

    FriendshipStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
