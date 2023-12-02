package com.lq.im.common.enums.friendship;


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
    BLOCK_STATUS_NORMAL(1),

    BLOCK_STATUS_BLOCKED(2),
    ;


    private final int code;

    FriendshipStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
