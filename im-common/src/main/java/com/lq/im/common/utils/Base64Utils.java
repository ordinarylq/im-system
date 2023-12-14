package com.lq.im.common.utils;

import java.util.Base64;

public class Base64Utils {

    public static String encode(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }

    public static String encodeWithoutPadding(byte[] input) {
        return Base64.getEncoder().withoutPadding().encodeToString(input);
    }

    public static byte[] decode(String encodedString) {
        return Base64.getDecoder().decode(encodedString);
    }
}
