package com.lq.im.service.utils;


import com.alibaba.fastjson.JSONObject;
import com.lq.im.common.constant.Constants;
import com.lq.im.common.enums.gateway.ImConnecStatusEnum;
import com.lq.im.common.model.UserClientDTO;
import com.lq.im.common.model.UserSession;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class UserSessionUtils {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public List<UserSession> getUserSession(Integer appId, String userId)  {
        String key = getKeyOfUserSession(appId, userId);
        Map<Object, Object> entries = this.stringRedisTemplate.opsForHash().entries(key);
        ArrayList<UserSession> sessionList = new ArrayList<>();
        entries.forEach((hashKey, value) -> {
            UserSession userSession = JSONObject.parseObject((String) value, UserSession.class);
            if (userSession.getConnectStatus() == ImConnecStatusEnum.ONLINE_STATUS.getCode()) {
                sessionList.add(userSession);
            }
        });
        return sessionList;
    }

    public UserSession getUserSession(UserClientDTO userClient) {
        String key = getKeyOfUserSession(userClient.getAppId(), userClient.getUserId());
        String hashKey = getHashKeyOfUserSession(userClient.getClientType(), userClient.getImei());
        String value = (String) this.stringRedisTemplate.opsForHash().get(key, hashKey);
        return JSONObject.parseObject(value, UserSession.class);
    }

    private static String getKeyOfUserSession(Integer appId, String userId) {
        return appId + Constants.RedisConstants.USER_SESSION + userId;
    }

    private static String getHashKeyOfUserSession(Integer clientType, String imei) {
        return clientType + ":" + imei;
    }

}
