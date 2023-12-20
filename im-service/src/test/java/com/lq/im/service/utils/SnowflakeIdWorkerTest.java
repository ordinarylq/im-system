package com.lq.im.service.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class SnowflakeIdWorkerTest {

    private SnowflakeIdWorker snowflakeIdWorker;

    @BeforeEach
    void init() {
        this.snowflakeIdWorker = new SnowflakeIdWorker(0L, 0L);
    }

    @Test
    void testGenerateId() {
        Long nextId = this.snowflakeIdWorker.getNextId();
        log.info("Generate next id {}", nextId);
        assertNotNull(nextId);
    }

    @Test
    void testGenerateMultipleIds() {
        int count = (int) Math.pow(10, 7);
        Set<Long> idSet = new HashSet<>(count);
        for (int i = 0; i < count; i++) {
            Long nextId = this.snowflakeIdWorker.getNextId();
            assertFalse(idSet.contains(nextId));
            idSet.add(nextId);
        }
    }
}
