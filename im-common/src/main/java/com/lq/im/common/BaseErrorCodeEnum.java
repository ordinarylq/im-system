package com.lq.im.common;

import com.lq.im.common.exception.ApplicationExceptionEnum;

/**
 * @ClassName: BaseErrorCodeEnum
 * @Author: LiQi
 * @Date: 2023-04-11 11:23
 * @Version: V1.0
 * @Description:
 */
public enum BaseErrorCodeEnum implements ApplicationExceptionEnum {

    SUCCESS(200,"success"),
    SYSTEM_ERROR(90000,"服务器内部错误,请联系管理员"),
    PARAMETER_ERROR(90001,"参数校验错误");

    private final int code;
    private final String error;

    BaseErrorCodeEnum(int code, String error){
        this.code = code;
        this.error = error;
    }
    public int getCode() {
        return this.code;
    }

    public String getError() {
        return this.error;
    }
}
