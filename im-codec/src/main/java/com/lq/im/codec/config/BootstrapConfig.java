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
        /**
         * 客户端超时时间(ms)
         */
        private Long timeout;
        /**
         * IM集群节点id
         */
        private Integer brokerId;
        /**
         * 多端登录模式
         * 移动端（Android, iPhone, iPad）,桌面端（Windows, Mac）, Web端
         * 1-单平台登录。仅允许单端登录。
         * 2-双平台登录。移动端或桌面端、Web端。
         * 3-三平台登录。移动端、桌面端、web端。
         * 4-多平台登录。三端可同时在线。
         */
        private Integer loginMode;

        private RedisConfig redis;
        private RabbitMQConfig rabbitmq;
        private ZooKeeperConfig zooKeeper;
        /**
         * 逻辑层ip:port
         */
        private String logicUrl;
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

    @Data
    public static class RabbitMQConfig {
        private String host;
        private Integer port;
        private String username;
        private String password;
        private String virtualHost;
    }

    @Data
    public static class ZooKeeperConfig {
        private String host;
        private Integer connectTimeout;
    }

}
