package com.lq.im.tcp.redis;

import com.lq.im.codec.config.BootstrapConfig;
import org.redisson.api.RedissonClient;

public class RedisManager {
    private static RedissonClient redissonClient;

    public static void init(BootstrapConfig config) {
        redissonClient =
                new SingleRedisStrategy(config.getIm().getRedis()).getRedissonClient();
    }

    public static RedissonClient getRedissonClient() {
        return redissonClient;
    }
}
