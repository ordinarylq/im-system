package com.lq.im.tcp.utils;

import com.lq.im.common.model.UserClientDTO;
import io.netty.channel.socket.nio.NioSocketChannel;

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

}
