package com.lq.im.service.router.handler;

import com.lq.im.common.exception.ApplicationException;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.lq.im.common.enums.user.UserErrorCodeEnum.SERVER_NOT_AVAILABLE;

public abstract class RouteHandler {

    /**
     * 选择合适的算法，根据key从serverList中获取到服务器地址
     * @param serverList 服务器地址列表
     * @param key key
     * @return 服务器地址
     */
    public String chooseServer(List<String> serverList, String key) {
        if (CollectionUtils.isEmpty(serverList)) {
            throw new ApplicationException(SERVER_NOT_AVAILABLE);
        }
        return doChoose(serverList, key);
    }

    public abstract String doChoose(List<String> serverList, String key);
}

