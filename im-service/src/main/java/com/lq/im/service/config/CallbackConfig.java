package com.lq.im.service.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@EnableConfigurationProperties({HttpClientProperties.class})
public class CallbackConfig {
    @Resource
    private HttpClientProperties httpClientProperties;

    @Bean("poolingHttpClientConnectionManager")
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(httpClientProperties.getMaxTotal());
        manager.setDefaultMaxPerRoute(httpClientProperties.getDefaultMaxPerRoute());
        return manager;
    }

    @Bean("httpClientBuilder")
    public HttpClientBuilder httpClientBuilder(
            @Qualifier("poolingHttpClientConnectionManager") PoolingHttpClientConnectionManager manager
    ) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(manager);
        return httpClientBuilder;
    }

    @Bean("closableHttpClient")
    public CloseableHttpClient closableHttpClient(
            @Qualifier("httpClientBuilder") HttpClientBuilder httpClientBuilder
    ) {
        return httpClientBuilder.build();
    }

    @Bean("requestConfigBuilder")
    public RequestConfig.Builder requestConfigBuilder() {
        RequestConfig.Builder builder = RequestConfig.custom();
        return builder.setConnectTimeout(httpClientProperties.getConnectTimeout())
                .setSocketTimeout(httpClientProperties.getSocketTimeout());
    }

    @Bean
    public RequestConfig requestConfig(@Qualifier("requestConfigBuilder") RequestConfig.Builder requestConfigBuilder) {
        return requestConfigBuilder.build();
    }
}
