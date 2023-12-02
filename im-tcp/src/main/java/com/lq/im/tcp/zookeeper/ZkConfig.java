package com.lq.im.tcp.zookeeper;

import com.lq.im.codec.config.BootstrapConfig;
import static com.lq.im.common.constant.Constants.ZkConstants.*;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class ZkConfig {

    private ZkClientUtils zkClientUtils;
    private BootstrapConfig config;
    private String localIpAddress;
    private Integer tcpPort;
    private Integer websocketPort;

    public ZkConfig(BootstrapConfig config) throws UnknownHostException {
        this.config = config;
        initIpAndPort();
    }

    private void initIpAndPort() throws UnknownHostException {
        localIpAddress = InetAddress.getLocalHost().getHostAddress();
        tcpPort = config.getIm().getTcpPort();
        websocketPort = config.getIm().getWebsocketPort();
    }

    public void init() {
        BootstrapConfig.ZooKeeperConfig zooKeeperConfig = config.getIm().getZooKeeper();
        ZkClient zkClient = new ZkClient(zooKeeperConfig.getHost(), zooKeeperConfig.getConnectTimeout());
        zkClientUtils = new ZkClientUtils(zkClient);
        threadStart();
    }

    private void threadStart() {
        new Thread(() -> {
            zkClientUtils.createBasicNodes();
            String tcpPath = ROOT_NODE + TCP_NODE + "/" + localIpAddress + ":" + tcpPort;
            registerNode(tcpPath);
            String websocketPath = ROOT_NODE + WEB_NODE + "/" + localIpAddress + ":" + websocketPort;
            registerNode(websocketPath);
        }).start();
    }

    private void registerNode(String path) {
        zkClientUtils.createNode(path);
        log.info("Successfully register to Zookeeper with path [{}]", path);
    }
}
