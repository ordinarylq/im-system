package com.lq.im.service.router.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "router")
@Data
public class RouterProperties {
    /**
     * IP:port
     */
    private String zkHost;

    /**
     * 连接超时时间(ms)
     */
    private Integer zkConnectTimeout = 5000;

    /**
     * 服务器路由选择策略
     * 0-随机 1-轮询 2-一致性hash
     */
    private Integer routeStrategy = 0;

    /**
     * 选择策略为一致性hash时的hash策略 0-TreeMap 1-其他
     */
    private Integer hashingStrategy = 1;

    /**
     * 自定义Hash策略全类名
     * 需要继承com.lq.im.service.router.handler.hash.AbstractHashing
     */
    private String customHashingStrategy = "";
}
