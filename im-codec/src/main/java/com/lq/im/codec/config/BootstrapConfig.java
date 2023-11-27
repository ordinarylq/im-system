package com.lq.im.codec.config;

import lombok.Data;

@Data
public class BootstrapConfig {

    private TcpConfig im;

    @Data
    public static class TcpConfig {
        private Integer tcpPort;
        private Integer websocketPort;
        private Integer bossThreadSize;
        private Integer workerThreadSize;
        private Long timeout;
        private RedisConfig redis;
    }

    @Data
    public static class RedisConfig {
        /**
         * Redis模式
         * single-单机模式，sentinel-哨兵模式，cluster-集群模式
         */
        private String mode;
        /**
         * Redis数据库
         */
        private Integer database;
        /**
         * Redis登录密码
         */
        private String password;
        /**
         * 超时时间
         */
        private Integer timeout;
        /**
         *连接池最小空闲连接数
         */
        private Integer poolMinIdle;
        /**
         * 连接超时时间(ms)
         */
        private Integer poolConnTimeout;
        /**
         * 连接池大小
         */
        private Integer poolSize;
        /**
         * Redis单机配置
         */
        private RedisSingle single;
    }

    @Data
    public static class RedisSingle {
        /**
         * 地址:端口号
         */
        private String address;
    }

}
