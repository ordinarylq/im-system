package com.lq.im.common.enums;

import com.lq.im.common.exception.ApplicationExceptionEnum;

/**
 * @ClassName: UserErrorCodeEnum
 * @Author: LiQi
 * @Date: 2023-04-12 13:59
 * @Version: V1.0
 * @Description:
 */
public enum UserErrorCodeEnum implements ApplicationExceptionEnum {
    IMPORT_SIZE_BEYOND(20000,"导入数量超出上限"),
    USER_IS_NOT_EXIST(20001,"用户不存在"),
    SERVER_GET_USER_ERROR(20002,"服务获取用户失败"),
    MODIFY_USER_ERROR(20003,"更新用户失败"),
    SERVER_NOT_AVAILABLE(71000, "没有可用的服务"),
    REQUEST_DATA_IS_NOT_EXIST(20004, "请求数据不存在"),
    ;


    private final int code;
    private final String error;

    UserErrorCodeEnum(int code, String error) {
        this.code = code;
        this.error = error;
    }

    @Override
    public int getCode() {
        return 0;
    }

    @Override
    public String getError() {
        return null;
    }
}
