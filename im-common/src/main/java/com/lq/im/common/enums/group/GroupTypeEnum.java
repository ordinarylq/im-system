package com.lq.im.common.enums.group;

public enum GroupTypeEnum {
    /**
     * 私有群
     * <p>
     * 类似微信群，只能通过群内好友邀请的方式拉人入群
     */
    PRIVATE(1),
    /**
     * 公开群
     * <p>
     * 类似QQ群，用户可以通过搜索群ID发起申请入群
     */
    PUBLIC(2),
    ;
    private final int code;

    GroupTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static GroupTypeEnum getItemByCode(int code) {
        for (int i = 0; i < GroupTypeEnum.values().length; i++) {
            if(code == GroupTypeEnum.values()[i].getCode()) {
                return GroupTypeEnum.values()[i];
            }
        }
        return null;
    }
}
