package com.lq.im.common.enums;

/**
 * @ClassName: GroupMuteTypeEnum
 * @Author: LiQi
 * @Date: 2023-06-01 14:48
 * @Version: V1.0
 */
public enum GroupMuteTypeEnum {
    /**
     * 未开启全员禁言
     */
    NOT_MUTE(0),

    /**
     * 已开启全员禁言
     */
    MUTE(1)
    ;
    private final int code;

    GroupMuteTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    /**
     * 根据code获取指定的枚举对象
     * @author LiQi
     * @param code the code
     * @return GroupMuteTypeEnum
     */
    public static GroupMuteTypeEnum getItemByCode(int code) {
        for (int i = 0; i < GroupMuteTypeEnum.values().length; i++) {
            if(code == GroupMuteTypeEnum.values()[i].getCode()) {
                return GroupMuteTypeEnum.values()[i];
            }
        }
        return null;
    }
}
