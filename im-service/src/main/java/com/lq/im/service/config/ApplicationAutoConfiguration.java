package com.lq.im.service.config;

import com.lq.im.service.utils.SnowflakeIdWorker;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties({ApplicationConfigProperties.class, HttpClientProperties.class})
public class ApplicationAutoConfiguration {

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

    @Bean
    public MyBatisPlusSqlInjector myBatisPlusSqlInjector() {
        return new MyBatisPlusSqlInjector();
    }

    @Bean
    public SnowflakeIdWorker snowflakeIdWorker() {
        return new SnowflakeIdWorker(0L, 0L);
    }

    @Bean
    public ThreadPoolExecutor fixedMsgProcessThreadPool() {
        return new ThreadPoolExecutor(
                10,
                10,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
