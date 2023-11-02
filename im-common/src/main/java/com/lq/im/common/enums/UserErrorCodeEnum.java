package com.lq.im.common.enums;

import com.lq.im.common.exception.ApplicationExceptionEnum;

public enum UserErrorCodeEnum implements ApplicationExceptionEnum {

    TOO_MUCH_DATA(TOO_MUCH_DATA_CODE, TOO_MUCH_DATA_MESSAGE),
    USER_IS_NOT_EXIST(USER_IS_NOT_EXIST_CODE, USER_IS_NOT_EXIST_MESSAGE),
    SERVER_GET_USER_ERROR(SERVER_GET_USER_ERROR_CODE, SERVER_GET_USER_ERROR_MESSAGE),
    MODIFY_USER_ERROR(MODIFY_USER_ERROR_CODE, MODIFY_USER_ERROR_MESSAGE),
    REQUEST_DATA_DOES_NOT_EXIST(REQUEST_DATA_DOES_NOT_EXIST_CODE, REQUEST_DATA_DOES_NOT_EXIST_MESSAGE),
    SERVER_NOT_AVAILABLE(SERVER_NOT_AVAILABLE_CODE, SERVER_NOT_AVAILABLE_MESSAGE),
    ;

    private final int code;
    private final String error;

    UserErrorCodeEnum(int code, String error) {
        this.code = code;
        this.error = error;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getError() {
        return this.error;
    }
}
