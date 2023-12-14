package com.lq.im.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class HmacUtils {

    public static final String DEFAULT_HASH_ALGORITHM = "HmacSHA256";

    private static String hashAlgorithm = DEFAULT_HASH_ALGORITHM;

    public static String hash(String privateKey, String input) {
        SecretKeySpec secretKey = new SecretKeySpec(privateKey.getBytes(StandardCharsets.UTF_8), hashAlgorithm);
        Mac mac;
        try {
            mac = Mac.getInstance(hashAlgorithm);
            mac.init(secretKey);
            byte[] result = mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return Base64Utils.encodeWithoutPadding(result);
        } catch (NoSuchAlgorithmException e) {
            log.error("There's no such hashing algorithm: {}", e.getMessage());
        } catch (InvalidKeyException e) {
            log.error("Invalid key while hashing: {}", e.getMessage());
        }
        return "";
    }

    public static void setHashAlgorithm(String hashAlgorithm) {
        HmacUtils.hashAlgorithm = hashAlgorithm;
    }
}
