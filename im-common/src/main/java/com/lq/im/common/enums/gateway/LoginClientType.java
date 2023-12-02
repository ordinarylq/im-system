package com.lq.im.common.enums.gateway;

/**
 * 多端登录模式
 * 移动端（Android, iPhone, iPad）,桌面端（Windows, Mac）, Web端
 * 1-单平台登录。仅允许单端登录。
 * 2-双平台登录。移动端或桌面端、Web端。其中web端设备不限制数量。
 * 3-三平台登录。移动端、桌面端、web端。其中web端设备不限制数量。
 * 4-多平台登录。多端可同时在线。
 */
public enum LoginClientType {
    ONE(1, "One type of client"),
    TWO(2, "Two types of clients"),
    THREE(3, "Three platforms of clients"),
    FOUR(4, "All platforms of clients")
    ;

    final int code;
    final String description;

    LoginClientType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
