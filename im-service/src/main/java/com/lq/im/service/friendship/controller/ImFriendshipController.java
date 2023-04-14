package com.lq.im.service.friendship.controller;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.friendship.model.req.AddFriendReq;
import com.lq.im.service.friendship.model.req.ImportFriendshipReq;
import com.lq.im.service.friendship.service.ImFriendshipService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sun.plugin2.main.server.AppletID;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @ClassName: ImFriendshipController
 * @Author: LiQi
 * @Date: 2023-04-14 8:33
 * @Version: V1.0
 * @Description:
 */
@RestController
@RequestMapping("/api/v1/friendship")
public class ImFriendshipController {

    @Resource
    private ImFriendshipService imFriendshipService;

    @PostMapping("/import")
    public ResponseVO importFriendship(@RequestBody @Valid ImportFriendshipReq req, @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return this.imFriendshipService.importFriendship(req);
    }

    @PostMapping("/add")
    public ResponseVO addFriendship(@RequestBody @Valid AddFriendReq req, @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return this.imFriendshipService.addFriend(req);
    }
}
