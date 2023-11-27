package com.lq.im.tcp.redis;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

public class RedissonTest {
    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.145.128:6379");
        config.setCodec(new StringCodec());
        RedissonClient redissonClient = Redisson.create(config);
        // string
        RBucket<Object> im = redissonClient.getBucket("im");
        System.out.println(im.get());
        im.set("test");
        System.out.println(im.get());

        // hash
        RMap<String, String> imMap = redissonClient.getMap("imMap");
        String client = imMap.get("client");
        System.out.println(client);
        imMap.put("client", "webClient");
        System.out.println(imMap.get("client"));

        // 发布订阅
        RTopic topic = redissonClient.getTopic("im");
        topic.addListener(String.class, (charSequence, s) -> System.out.println("收到消息：" + s));
        topic.publish("Hello, Redis!");


    }
}
