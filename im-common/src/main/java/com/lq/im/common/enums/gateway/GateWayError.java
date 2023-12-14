package com.lq.im.common.enums.gateway;

import com.lq.im.common.exception.ApplicationExceptionEnum;

public enum GateWayError implements ApplicationExceptionEnum {

    APPID_NOT_EXIST(60000, "应用ID不存在"),
    OPERATOR_ID_NOT_EXIST(60001, "操作人ID不存在"),
    USER_SIGNATURE_NOT_EXIST(60002, "用户签名不存在"),
    USER_SIGNATURE_ERROR(60003, "用户签名有误"),
    OPERATOR_DOES_NOT_MATCH_SIGNATURE(60004, "操作人与用户签名不匹配"),
    USER_SIGNATURE_EXPIRED(60005, "用户签名已过期")
    ;

    private final int code;
    private final String error;

    GateWayError(int code, String error) {
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
