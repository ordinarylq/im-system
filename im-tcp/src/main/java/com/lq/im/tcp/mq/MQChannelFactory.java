package com.lq.im.tcp.mq;

import com.lq.im.codec.config.BootstrapConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

public class MQChannelFactory {
    private static ConnectionFactory factory;

    private static Channel defaultChannel;

    private static ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();

    public static void init(BootstrapConfig.RabbitMQConfig config) {
        if (factory == null) {
            synchronized (MQChannelFactory.class) {
                if (factory == null) {
                    factory = new ConnectionFactory();
                    factory.setHost(config.getHost());
                    factory.setPort(config.getPort());
                    factory.setUsername(config.getUsername());
                    factory.setPassword(config.getPassword());
                    factory.setVirtualHost(config.getVirtualHost());
                }
            }
        }
    }
    public static Channel getChannel(String channelName) throws IOException, TimeoutException {
        Channel channel = channelMap.get(channelName);
        if (channel == null) {
            channel = getConnection().createChannel();
            channelMap.put(channelName, channel);
        }
        return channel;
    }

    private static Connection getConnection() throws IOException, TimeoutException {
        return factory.newConnection();
    }

}
