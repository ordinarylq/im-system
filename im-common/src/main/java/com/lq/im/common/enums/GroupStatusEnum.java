package com.lq.im.common.enums;

/**
 * @ClassName: GroupStatusEnum
 * @Author: LiQi
 * @Date: 2023-06-01 14:55
 * @Version: V1.0
 * @Description:
 */
public enum GroupStatusEnum {
    /**
     * 正常
     */
    NORMAL(0),
    /**
     * 已解散
     */
    DISMISS(1),
    ;
    private final int code;

    GroupStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
    /**
     * 根据code获取指定的枚举对象
     * @author LiQi
     * @param code the code
     * @return GroupStatusEnum
     */
    public static GroupStatusEnum getItemByCode(int code) {
        for (int i = 0; i < GroupStatusEnum.values().length; i++) {
            if(code == GroupStatusEnum.values()[i].getCode()) {
                return GroupStatusEnum.values()[i];
            }
        }
        return null;
    }
}
