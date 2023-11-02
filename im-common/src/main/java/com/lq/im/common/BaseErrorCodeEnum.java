package com.lq.im.common;

import com.lq.im.common.exception.ApplicationExceptionEnum;

public enum BaseErrorCodeEnum implements ApplicationExceptionEnum {

    SUCCESS(SUCCESS_CODE,SUCCESS_MESSAGE),
    INTERNAL_ERROR(INTERNAL_ERROR_CODE, INTERNAL_ERROR_MESSAGE),
    PARAMETER_ERROR(PARAMETER_VALIDATION_ERROR_CODE,PARAMETER_VALIDATION_ERROR_MESSAGE);

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
