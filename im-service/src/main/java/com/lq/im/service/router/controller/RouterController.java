package com.lq.im.service.router.controller;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.router.service.RouterService;
import com.lq.im.service.user.model.req.LoginReq;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/v1/router/")
public class RouterController {
    @Resource
    private RouterService routerService;

    @GetMapping("/server")
    public ResponseVO<?> getServer(@RequestBody LoginReq req, @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return this.routerService.getServerAddress(req);
    }
}
