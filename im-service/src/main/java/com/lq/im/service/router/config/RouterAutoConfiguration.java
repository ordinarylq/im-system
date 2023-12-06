package com.lq.im.service.router.config;

import com.lq.im.common.enums.gateway.ImServerRouteHashingMethod;
import com.lq.im.common.enums.gateway.ImServerRouteMethod;
import com.lq.im.service.router.handler.ConsistentHashRouteHandler;
import com.lq.im.service.router.handler.RouteHandler;
import com.lq.im.service.router.handler.hash.AbstractHashing;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;


@Slf4j
@Configuration
@EnableConfigurationProperties({RouterProperties.class})
public class RouterAutoConfiguration {

    @Resource
    private RouterProperties routerProperties;

    @Bean
    public ZkClient zkClient() {
        return new ZkClient(routerProperties.getZkHost(), routerProperties.getZkConnectTimeout());
    }

    @Bean
    public RouteHandler routeHandler() {
        Integer routeStrategy = routerProperties.getRouteStrategy();
        String routeStrategyClassName = ImServerRouteMethod.getByCode(routeStrategy).getClassName();
        try {
            RouteHandler routeHandler = (RouteHandler) Class.forName(routeStrategyClassName).newInstance();
            if (routeStrategy == ImServerRouteMethod.HASHING.getCode()) {
                Integer hashingStrategy = routerProperties.getHashingStrategy();
                AbstractHashing hashing = getHashingStrategy(hashingStrategy);
                ((ConsistentHashRouteHandler)routeHandler).setHashing(hashing);
            }
            return routeHandler;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            log.error("An error occurred while initializing RouteHandler.", e);
            throw new RuntimeException(e);
        }
    }

    private AbstractHashing getHashingStrategy(int strategyCode)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        ImServerRouteHashingMethod method = ImServerRouteHashingMethod.getByCode(strategyCode);
        AbstractHashing hashMethod = null;
        switch (method) {
            case TREEMAP:
                hashMethod = (AbstractHashing) Class.forName(method.getClassName()).newInstance();
                break;
            case CUSTOM:
                hashMethod = (AbstractHashing) Class.forName(routerProperties.getCustomHashingStrategy()).newInstance();
                break;
            default:
                /* ignore */
        }
        return hashMethod;
    }

}
