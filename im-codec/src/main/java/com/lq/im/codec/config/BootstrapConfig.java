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
    }

}
