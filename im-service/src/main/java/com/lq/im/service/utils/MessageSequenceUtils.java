package com.lq.im.service.utils;

public class MessageSequenceUtils {

    public static String getPeerToPeerRedisKey(String userId, String friendUserId) {
        return userId.compareTo(friendUserId) < 0 ? userId + "|" + friendUserId : friendUserId + "|" + userId;
    }
}
