package com.lq.im.tcp.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lq.im.codec.body.LoginMessageBody;
import com.lq.im.codec.proto.Message;
import com.lq.im.codec.proto.MessageHeader;
import com.lq.im.common.constant.Constants;
import com.lq.im.common.model.UserClientDTO;
import com.lq.im.tcp.redis.RedisManager;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class SessionHandler {
    private static final ConcurrentHashMap<UserClientDTO, NioSocketChannel> channels = new ConcurrentHashMap<>();

    public static List<NioSocketChannel> getUserRelatedChannelList(UserClientDTO userClientDTO) {
        return channels.entrySet().stream().filter(
                entry -> Objects.equals(entry.getKey().getAppId(), userClientDTO.getAppId())
                        && entry.getKey().getUserId().equals(userClientDTO.getUserId())
        ).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public static NioSocketChannel getChannel(UserClientDTO userClientDTO) {
        return channels.get(userClientDTO);
    }

    public static void login(NioSocketChannel channel, Message msg, Integer brokerId) {
        MessageHeader header = msg.getHeader();
        LoginMessageBody loginMessageBody = JSON.parseObject(
                JSONObject.toJSONString(msg.getBody()), new TypeReference<LoginMessageBody>() {}.getType());
        setChannelAttributes(channel, header, loginMessageBody);
        UserClientDTO userClientDTO = new UserClientDTO(header.getAppId(), header.getClientType(),
                loginMessageBody.getUserId(), header.getImei());
        channels.put(userClientDTO, channel);
        RedisManager.sendLoginMessageToChannel(userClientDTO);
        RedisManager.addSessionToRedis(loginMessageBody, header, brokerId);
    }

    private static void setChannelAttributes(NioSocketChannel channel, MessageHeader header, LoginMessageBody loginMessageBody) {
        setChannelAttribute(channel, Constants.APP_ID, header.getAppId());
        setChannelAttribute(channel, Constants.CLIENT_TYPE, header.getClientType());
        setChannelAttribute(channel, Constants.USER_ID, loginMessageBody.getUserId());
        setChannelAttribute(channel, Constants.DEVICE_IMEI, header.getImei());
        setChannelAttribute(channel, Constants.LAST_READ_TIME, System.currentTimeMillis());
    }
    private static void setChannelAttribute(NioSocketChannel channel, String key, Object value) {
        channel.attr(AttributeKey.valueOf(key)).set(value);
    }

    /**
     * 退出登录
     * 需要删除内存session, Redis中的session
     */
    public static void logout(NioSocketChannel channel) {
        UserClientDTO userClientDTO = removeSessionInMemory(channel);
        RedisManager.removeSessionFromRedis(userClientDTO);
        channel.close();
    }

    /**
     * 超时离线
     * 只需要删除内存中的session
     */
    public static void offline(NioSocketChannel channel) {
        UserClientDTO userClientDTO = removeSessionInMemory(channel);
        RedisManager.updateOfflineSessionToRedis(userClientDTO);
        channel.close();
    }

    private static UserClientDTO removeSessionInMemory(NioSocketChannel channel) {
        Integer appId = getChannelAttribute(channel, Constants.APP_ID, Integer.class);
        Integer clientType = getChannelAttribute(channel, Constants.CLIENT_TYPE, Integer.class);
        String userId = getChannelAttribute(channel, Constants.USER_ID, String.class);
        String imei = getChannelAttribute(channel, Constants.DEVICE_IMEI, String.class);
        UserClientDTO userClientDTO = new UserClientDTO(appId, clientType, userId, imei);
        channels.remove(userClientDTO);
        return userClientDTO;
    }

    @SuppressWarnings("unchecked")
    private static <T> T getChannelAttribute(NioSocketChannel channel, String key, Class<T> aClass) {
        return (T) channel.attr(AttributeKey.valueOf(key)).get();
    }

}
