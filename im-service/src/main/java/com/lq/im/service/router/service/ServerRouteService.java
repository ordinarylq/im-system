package com.lq.im.service.router.service;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.user.model.req.LoginReq;

public interface ServerRouteService {

    ResponseVO<?> getServerAddress(LoginReq req);
}
