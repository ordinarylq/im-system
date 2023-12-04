package com.lq.im.service.router.service.impl;

import com.lq.im.common.ResponseVO;
import com.lq.im.common.enums.gateway.LoginDeviceType;
import com.lq.im.service.router.handler.PollingRouteHandler;
import com.lq.im.service.router.handler.RouteHandler;
import com.lq.im.service.router.handler.ServerInfo;
import com.lq.im.service.router.service.RouterService;
import com.lq.im.service.router.zookeeper.ZKClientUtils;
import com.lq.im.service.user.model.req.LoginReq;
import com.lq.im.service.user.service.ImUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RouterServiceImpl implements RouterService {

    @Resource
    private ImUserService imUserService;

    @Resource
    private ZKClientUtils zkClientUtils;

    @Override
    public ResponseVO<?> getServerAddress(LoginReq req) {
        ResponseVO<?> responseVO = this.imUserService.login(req);
        if (responseVO.isOk()) {
            List<String> serverList;
            if (req.getClientType() == LoginDeviceType.WEB.getCode()) {
                serverList = this.zkClientUtils.getAllWebsocketNodes();
            } else {
                serverList = this.zkClientUtils.getAllTcpNodes();
            }
            RouteHandler routeHandler = new PollingRouteHandler();
            String targetServerAddress = routeHandler.chooseServer(serverList, req.getUserId());
            return ResponseVO.successResponse(ServerInfo.parse(targetServerAddress));
        }
        return responseVO;
    }
}
