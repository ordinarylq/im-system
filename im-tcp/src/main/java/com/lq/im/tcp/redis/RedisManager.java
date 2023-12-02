package com.lq.im.tcp.redis;

import com.alibaba.fastjson.JSONObject;
import com.lq.im.codec.body.LoginMessageBody;
import com.lq.im.codec.config.BootstrapConfig;
import com.lq.im.codec.proto.MessageHeader;
import com.lq.im.common.constant.Constants;
import com.lq.im.common.enums.gateway.ImConnecStatusEnum;
import com.lq.im.common.model.UserClientDTO;
import com.lq.im.common.model.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import java.net.Inet4Address;
import java.net.UnknownHostException;

@Slf4j
public class RedisManager {
    private static RedissonClient redissonClient;
    private static RTopic userLoginTopic;

    public static void init(BootstrapConfig config) {
        redissonClient =
                new SingleRedisStrategy(config.getIm().getRedis()).getRedissonClient();
        userLoginTopic = redissonClient.getTopic(Constants.RedisConstants.USER_LOGIN_CHANNEL);
        userLoginTopic.addListener(String.class, new RedisChannelListener(config.getIm().getLoginMode()));
    }

    /**
     * 添加UserSession对象字符串到Redis
     */
    public static void addSessionToRedis(LoginMessageBody loginMessageBody, MessageHeader header, Integer brokerId) {
        String localHostAddress;
        try {
            localHostAddress = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("An error occurred while fetching local ip address.", e);
            throw new RuntimeException(e);
        }
        UserSession userSession = new UserSession(loginMessageBody.getUserId(), header.getAppId(), header.getClientType(), header.getVersion(),
                ImConnecStatusEnum.ONLINE_STATUS.getCode(), brokerId, localHostAddress);
        String hashKey = header.getAppId() + Constants.RedisConstants.USER_SESSION + loginMessageBody.getUserId();
        RMap<String, String> map = redissonClient.getMap(hashKey);
        map.put(header.getClientType() + ":" + header.getImei(), JSONObject.toJSONString(userSession));
    }

    /**
     * 从Redis中移除指定的UserSession对象字符串
     */
    public static void removeSessionFromRedis(UserClientDTO userClientDTO) {
        String hashKey = userClientDTO.getAppId() + Constants.RedisConstants.USER_SESSION + userClientDTO.getUserId();
        redissonClient.getMap(hashKey).remove(userClientDTO.getClientType() + ":" + userClientDTO.getImei());
    }

    /**
     * 更新Redis中UserSession对象字符串
     */
    public static void updateOfflineSessionToRedis(UserClientDTO userClientDTO) {
        String hashKey = userClientDTO.getAppId() + Constants.RedisConstants.USER_SESSION + userClientDTO.getUserId();
        String session = (String) redissonClient.getMap(hashKey).get(userClientDTO.getClientType() + ":" + userClientDTO.getImei());
        if (StringUtils.isNotBlank(session)) {
            UserSession userSession = JSONObject.parseObject(session, UserSession.class);
            userSession.setConnectStatus(ImConnecStatusEnum.OFFLINE_STATUS.getCode());
            redissonClient.getMap(hashKey).put(
                    String.valueOf(userClientDTO.getClientType()), JSONObject.toJSONString(userSession));
        }
    }

    public static void sendLoginMessageToChannel(UserClientDTO userClientDTO) {
        userLoginTopic.publish(JSONObject.toJSONString(userClientDTO));
    }
}
