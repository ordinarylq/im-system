package com.lq.im.tcp.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lq.im.codec.body.LoginMessageBody;
import com.lq.im.codec.proto.Message;
import com.lq.im.codec.proto.MessageHeader;
import com.lq.im.common.constant.Constants;
import com.lq.im.common.enums.ImConnecStatusEnum;
import com.lq.im.common.model.UserClientDTO;
import com.lq.im.common.model.UserSession;
import com.lq.im.tcp.redis.RedisManager;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.concurrent.ConcurrentHashMap;

public class SessionSocketHolder {
    private static final ConcurrentHashMap<UserClientDTO, NioSocketChannel> channels = new ConcurrentHashMap<>();

    public static void put(Integer appId, Integer clientType, String userId, NioSocketChannel channel) {
        channels.put(new UserClientDTO(appId, clientType, userId), channel);
    }

    public static NioSocketChannel get(Integer appId, Integer clientType, String userId) {
        return channels.get(new UserClientDTO(appId, clientType, userId));
    }

    public static void remove(Integer appId, Integer clientType, String userId) {
        channels.remove(new UserClientDTO(appId, clientType, userId));
    }

    public static void remove(NioSocketChannel channel) {
        channels.entrySet().stream().filter(entry -> entry.getValue() == channel)
                .forEach(entry -> channels.remove(entry.getKey()));
    }

    public static void login(NioSocketChannel channel, Message msg) {
        MessageHeader header = msg.getHeader();
        LoginMessageBody loginMessageBody = JSON.parseObject(JSONObject.toJSONString(msg.getBody()),
                new TypeReference<LoginMessageBody>() {}.getType());
        setChannelAttribute(channel, Constants.APP_ID, header.getAppId());
        setChannelAttribute(channel, Constants.CLIENT_TYPE, header.getClientType());
        setChannelAttribute(channel, Constants.USER_ID, loginMessageBody.getUserId());
        setChannelAttribute(channel, Constants.LAST_READ_TIME, System.currentTimeMillis());
        addSessionToRedis(loginMessageBody, header);
        SessionSocketHolder.put(header.getAppId(), header.getClientType(), loginMessageBody.getUserId(), channel);

    }

    /**
     * 退出登录
     * 需要删除内存session, Redis中的session
     */
    public static void logout(NioSocketChannel channel) {
        Integer appId = getChannelAttribute(channel, Constants.APP_ID, Integer.class);
        Integer clientType = getChannelAttribute(channel, Constants.CLIENT_TYPE, Integer.class);
        String userId = getChannelAttribute(channel, Constants.USER_ID, String.class);
        SessionSocketHolder.remove(appId, clientType, userId);
        removeSessionFromRedis(appId, clientType, userId);
        channel.close();
    }

    /**
     * 超时离线
     * 只需要删除内存中的session
     */
    public static void offline(NioSocketChannel channel) {
        Integer appId = getChannelAttribute(channel, Constants.APP_ID, Integer.class);
        Integer clientType = getChannelAttribute(channel, Constants.CLIENT_TYPE, Integer.class);
        String userId = getChannelAttribute(channel, Constants.USER_ID, String.class);
        SessionSocketHolder.remove(appId, clientType, userId);
        updateSessionToRedis(appId, clientType, userId);
        channel.close();
    }

    private static void setChannelAttribute(NioSocketChannel channel, String key, Object value) {
        channel.attr(AttributeKey.valueOf(key)).set(value);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getChannelAttribute(NioSocketChannel channel, String key, Class<T> aClass) {
        return (T) channel.attr(AttributeKey.valueOf(key)).get();
    }

    private static void addSessionToRedis(LoginMessageBody loginMessageBody, MessageHeader header) {
        UserSession userSession = new UserSession(loginMessageBody.getUserId(), header.getAppId(), header.getClientType(), header.getVersion(),
                ImConnecStatusEnum.ONLINE_STATUS.getCode());
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        String hashKey = header.getAppId() + Constants.RedisConstants.USER_SESSION + loginMessageBody.getUserId();
        RMap<String, String> map = redissonClient.getMap(hashKey);
        map.put(String.valueOf(header.getClientType()), JSONObject.toJSONString(userSession));
    }

    private static void removeSessionFromRedis(Integer appId, Integer clientType, String userId) {
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        String hashKey = appId + Constants.RedisConstants.USER_SESSION + userId;
        redissonClient.getMap(hashKey).remove(String.valueOf(clientType));
    }

    private static void updateSessionToRedis(Integer appId, Integer clientType, String userId) {
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        String hashKey = appId + Constants.RedisConstants.USER_SESSION + userId;
        String session = (String) redissonClient.getMap(hashKey).get(String.valueOf(clientType));
        if (StringUtils.isNotBlank(session)) {
            UserSession userSession = JSONObject.parseObject(session, UserSession.class);
            userSession.setConnectStatus(ImConnecStatusEnum.OFFLINE_STATUS.getCode());
            redissonClient.getMap(hashKey).put(String.valueOf(clientType), JSONObject.toJSONString(userSession));
        }
    }


}
