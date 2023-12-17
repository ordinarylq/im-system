package com.lq.im.common.enums.message;

import com.lq.im.common.exception.ApplicationExceptionEnum;

public enum MessageError implements ApplicationExceptionEnum {

    SENDER_IS_DISABLED(50000, "发送方账号已被禁用"),
    SENDER_IS_MUTED(50001, "发送方已被禁言"),

    ;

    private final int code;
    private final String error;

    MessageError(int code, String error) {
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
