package com.lq.im.common.enums.gateway;

import com.lq.im.common.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;

import static com.lq.im.common.BaseErrorCodeEnum.PARAMETER_ERROR;

@Slf4j
public enum ImServerRouteHashingMethod {
    TREEMAP(1, "com.lq.im.service.router.handler.hash.TreeMapHashing"),
    CUSTOM(2, "com.lq.im.service.router.handler.hash.YourHashing")
    ;

    private final int code;
    private final String className;

    ImServerRouteHashingMethod(int code, String className) {
        this.code = code;
        this.className = className;
    }

    public int getCode() {
        return code;
    }

    public String getClassName() {
        return className;
    }

    public static ImServerRouteHashingMethod getByCode(int code) {
        ImServerRouteHashingMethod[] values = ImServerRouteHashingMethod.values();
        for (ImServerRouteHashingMethod value : values) {
            if (value.getCode() == code) {
                return value;
            }
        }
        log.error("Parameter {} cannot be parsed.", "router.hashingStrategy");
        throw new ApplicationException(PARAMETER_ERROR);
    }
}
