package com.lq.im.tcp.zookeeper;

import static com.lq.im.common.constant.Constants.ZkConstants.*;
import org.I0Itec.zkclient.ZkClient;

public class ZkClientUtils {
    private ZkClient zkClient;

    public ZkClientUtils(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    public void createBasicNodes() {
        createNode(ROOT_NODE);
        String tcpNodePath = ROOT_NODE + TCP_NODE;
        createNode(tcpNodePath);
        String webNodePath = ROOT_NODE + WEB_NODE;
        createNode(webNodePath);
    }

    public void createNode(String path) {
        if (!zkClient.exists(path)) {
            zkClient.createPersistent(path);
        }
    }


}
