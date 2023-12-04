package com.lq.im.service.router.zookeeper;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zookeeper")
@Data
public class ZooKeeperProperties {
    /**
     * IP:port
     */
    private String host;

    /**
     * 连接超时时间(ms)
     */
    private Integer connectTimeout;
}
