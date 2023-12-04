package com.lq.im.service.router.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static com.lq.im.common.constant.Constants.ZkConstants.*;

@Component
@Slf4j
public class ZKClientUtils {

    @Resource
    private ZkClient zkClient;

    public List<String> getAllTcpNodes() {
        String tcpPath = ROOT_NODE + TCP_NODE;
        return this.zkClient.getChildren(tcpPath);
    }

    public List<String> getAllWebsocketNodes() {
        String websocketPath = ROOT_NODE + WEB_NODE;
        return this.zkClient.getChildren(websocketPath);
    }

}
