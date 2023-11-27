package com.lq.im.tcp.redis;

import com.lq.im.codec.config.BootstrapConfig;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

public class SingleRedisStrategy {
    private BootstrapConfig.RedisConfig redisConfig;

    public SingleRedisStrategy(BootstrapConfig.RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    public RedissonClient getRedissonClient() {
        Config config = new Config();
        String address = redisConfig.getSingle().getAddress();
        address = address.startsWith("redis://") ? address : "redis://" + address;
        SingleServerConfig singleServerConfig = config.useSingleServer()
                .setAddress(address)
                .setDatabase(redisConfig.getDatabase())
                .setTimeout(redisConfig.getTimeout())
                .setConnectionMinimumIdleSize(redisConfig.getPoolMinIdle())
                .setConnectTimeout(redisConfig.getPoolConnTimeout())
                .setConnectionPoolSize(redisConfig.getPoolSize());
        if (StringUtils.isNotBlank(redisConfig.getPassword())) {
            singleServerConfig.setPassword(redisConfig.getPassword());
        }
        config.setCodec(new StringCodec());
        return Redisson.create(config);
    }
}
