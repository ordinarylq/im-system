package com.lq.im.common.enums.gateway;

public enum LoginDeviceType {
    WEBAPI(0),
    WEB(1),
    IPHONE(2),
    IPAD(3),
    ANDROID(4),
    WINDOWS(5),
    MAC(6),
    ;

    final int code;

    LoginDeviceType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
