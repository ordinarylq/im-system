package com.lq.im.service.router.zookeeper;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@EnableConfigurationProperties({ZooKeeperProperties.class})
public class ZooKeeperAutoConfiguration {

    @Resource
    private ZooKeeperProperties zooKeeperProperties;

    @Bean
    public ZkClient zkClient() {
        return new ZkClient(zooKeeperProperties.getHost(), zooKeeperProperties.getConnectTimeout());
    }

}
