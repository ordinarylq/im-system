package com.lq.im.message.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public MyBatisPlusSqlInjector myBatisPlusSqlInjector() {
        return new MyBatisPlusSqlInjector();
    }
}
