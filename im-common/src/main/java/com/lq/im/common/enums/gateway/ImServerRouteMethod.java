package com.lq.im.common.enums.gateway;

import com.lq.im.common.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;

import static com.lq.im.common.BaseErrorCodeEnum.PARAMETER_ERROR;

@Slf4j
public enum ImServerRouteMethod {
    RANDOM(0, "com.lq.im.service.router.handler.RandomRouteHandler"),
    POLLING(1, "com.lq.im.service.router.handler.PollingRouteHandler"),
    HASHING(2, "com.lq.im.service.router.handler.ConsistentHashRouteHandler")
    ;

    private final int code;
    private final String className;

    ImServerRouteMethod(int code, String className) {
        this.code = code;
        this.className = className;
    }

    public int getCode() {
        return code;
    }

    public String getClassName() {
        return className;
    }

    public static ImServerRouteMethod getByCode(int code) {
        ImServerRouteMethod[] routeMethods = ImServerRouteMethod.values();
        for (ImServerRouteMethod routeMethod : routeMethods) {
            if (routeMethod.getCode() == code) {
                return routeMethod;
            }
        }
        log.error("Parameter {} cannot be parsed.", "router.routeStrategy");
        throw new ApplicationException(PARAMETER_ERROR);
    }
}
