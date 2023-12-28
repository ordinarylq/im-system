package com.lq.im.service.message.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RedisSequenceService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取消息的序列号，避免接收方消息乱序
     */
    public Long getSequence(String key) {
        return this.stringRedisTemplate.opsForValue().increment(key);
    }
}
