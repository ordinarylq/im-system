package com.lq.im.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app-config")
public class ApplicationConfigProperties {

    private String privateKey;

    private boolean enableSocialNetworkCheck;

    private boolean enableBlockListCheck;

}
