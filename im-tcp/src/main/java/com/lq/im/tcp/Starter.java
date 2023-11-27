package com.lq.im.tcp;

import com.lq.im.codec.config.BootstrapConfig;
import com.lq.im.tcp.server.ImServer;
import com.lq.im.tcp.server.ImWebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class Starter {
    /*
    1.请求头：指令 版本 clientType 消息类型 IMEI长度 appId bodyLen
    字节数：   04   4     4          4       4        4     4
    共28字节
    2. IMEI号
    3.请求体：json or protobuf
     */
    public static void main(String[] args) {
        System.out.println(Arrays.asList(args));
        if (args.length > 0) {
            new Starter().start(args[0]);
        }
    }

    public void start(String path) {
        Yaml yaml = new Yaml();
        try (FileInputStream fis = new FileInputStream(path)) {
            BootstrapConfig bootstrapConfig = yaml.loadAs(fis, BootstrapConfig.class);
            new ImServer(bootstrapConfig.getIm()).start();
            new ImWebSocketServer(bootstrapConfig.getIm()).start();
        } catch (IOException e) {
            log.error("Starting server failed.", e);
            System.exit(-1);
        }
    }
}
