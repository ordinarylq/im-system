package com.lq.im.common.enums.group;

public enum GroupMemberRoleEnum {
    /**
     * 普通成员
     */
    ORDINARY(0),
    /**
     * 管理员
     */
    MANAGER(1),
    /**
     * 群主
     */
    OWNER(2),
    /**
     * 已离开
     */
    LEAVE(3)
    ;

    private final int code;

    GroupMemberRoleEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static GroupMemberRoleEnum getItemByCode(int code) {
        for (int i = 0; i < GroupMemberRoleEnum.values().length; i++) {
            if(code == GroupMemberRoleEnum.values()[i].getCode()) {
                return GroupMemberRoleEnum.values()[i];
            }
        }
        return null;
    }
}
