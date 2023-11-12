package com.lq.im.common.enums;

public enum GroupTypeEnum {
    PRIVATE(1),
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
