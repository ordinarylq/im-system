package com.lq.im.common.enums.group;

public enum GroupStatusEnum {
    /**
     * 正常
     */
    NORMAL(0),
    /**
     * 已解散
     */
    DISMISSED(1),
    ;
    private final int code;

    GroupStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static GroupStatusEnum getItemByCode(int code) {
        for (int i = 0; i < GroupStatusEnum.values().length; i++) {
            if(code == GroupStatusEnum.values()[i].getCode()) {
                return GroupStatusEnum.values()[i];
            }
        }
        return null;
    }
}
