package com.lq.im.service.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserSignatureTest {

    private UserSignature userSignature;

    @BeforeEach
    void init() {
        userSignature = new UserSignature(1000, "test003", 1000000,
                System.currentTimeMillis(), null);
    }

    @Test
    void generateUserSig() {
        String signature = userSignature.generateUserSignature("123456");
        assertNotNull(signature);
        System.out.println(signature);
    }
}