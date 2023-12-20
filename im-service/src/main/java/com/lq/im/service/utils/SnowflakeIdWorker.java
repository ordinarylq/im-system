package com.lq.im.service.utils;

/**
 * Based on twitter snowflake algorithm.
 * @see <a href="https://github.com/twitter-archive/snowflake/blob/snowflake-2010/src/main/scala/com/twitter/service/snowflake/IdWorker.scala">snowflake</a>
 */
public class SnowflakeIdWorker {

    private static final Long SEQUENCE_ID_BITS = 12L;
    private static final Long WORKER_ID_BITS = 5L;
    private static final Long DATA_CENTER_ID_BITS = 5L;
    private static final Long TIMESTAMP_BITS = 41L;

    private static final Long WORKER_ID_BIT_OFFSET = SEQUENCE_ID_BITS;
    private static final Long DATA_CENTER_ID_BIT_OFFSET = SEQUENCE_ID_BITS + WORKER_ID_BITS;
    private static final Long TIMESTAMP_BIT_OFFSET = SEQUENCE_ID_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    private static final Long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final Long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);

    private static final Long SEQUENCE_MASK = ~(-1L << SEQUENCE_ID_BITS);

    private static final Long START_EPOCH = 1703071298853L;

    private Long workerId;
    private Long dataCenterId;
    private Long sequence = 0L;
    private Long lastTimestamp = -1L;

    public SnowflakeIdWorker(Long workerId, Long dataCenterId) {
        // sanity check
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException("workerId can't be less than 0 or greater than " + MAX_WORKER_ID);
        }
        if (dataCenterId < 0 || dataCenterId > MAX_DATA_CENTER_ID) {
            throw new IllegalArgumentException("dataCenterId can't be less than 0 or greater than " + MAX_DATA_CENTER_ID);
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    public synchronized Long getNextId() {
        long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                            lastTimestamp - currentTimestamp));
        }
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                currentTimestamp = tillNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = currentTimestamp;
        return (currentTimestamp - START_EPOCH) << TIMESTAMP_BIT_OFFSET
                | (dataCenterId << DATA_CENTER_ID_BIT_OFFSET)
                | (workerId << WORKER_ID_BIT_OFFSET)
                | sequence;
    }

    private Long tillNextMillis(Long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }






}
