package com.lq.im.common.enums;

/**
 * @ClassName: GroupTypeEnum
 * @Author: LiQi
 * @Date: 2023-06-01 14:57
 * @Version: V1.0
 */
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

    /**
     * 根据code获取指定的枚举对象
     * @author LiQi
     * @param code the code
     * @return GroupTypeEnum
     */
    public static GroupTypeEnum getItemByCode(int code) {
        for (int i = 0; i < GroupTypeEnum.values().length; i++) {
            if(code == GroupTypeEnum.values()[i].getCode()) {
                return GroupTypeEnum.values()[i];
            }
        }
        return null;
    }
}
