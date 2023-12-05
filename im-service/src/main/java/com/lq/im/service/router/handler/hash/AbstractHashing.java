package com.lq.im.service.router.handler.hash;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
public abstract class AbstractHashing {
    protected static final String DEFAULT_HASH_METHOD = "MD5";
    protected static String hashMethod = DEFAULT_HASH_METHOD;

    protected abstract void add(long key, String value);

    protected void sort() {}

    protected abstract String getNode(String userId);

    public synchronized String process(List<String> serverList, String userId) {
        beforeProcess();
        for (String serverAddress : serverList) {
            add(hash(serverAddress), serverAddress);
        }
        sort();
        return getNode(userId);
    }

    protected abstract void beforeProcess();

    protected long hash(String key) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(hashMethod);
        } catch (NoSuchAlgorithmException e) {
            log.error("Unknown hash algorithm {} ", hashMethod);
            throw new RuntimeException(e);
        }
        messageDigest.update(key.getBytes(StandardCharsets.UTF_8));
        byte[] result = messageDigest.digest();
        return bytesToLong(result);
    }

    protected static long bytesToLong(byte[] input) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            int shift = (7 - i) << 3;
            value |= ((long)(input[i] & 0xFF) << shift);
        }
        return value;
    }
}
