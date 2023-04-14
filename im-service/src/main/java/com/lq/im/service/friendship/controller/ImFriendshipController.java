package com.lq.im.service.friendship.controller;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.friendship.model.req.AddFriendshipReq;
import com.lq.im.service.friendship.model.req.DeleteFriendshipReq;
import com.lq.im.service.friendship.model.req.ImportFriendshipReq;
import com.lq.im.service.friendship.model.req.UpdateFriendshipReq;
import com.lq.im.service.friendship.service.ImFriendshipService;
import org.springframework.web.bind.annotation.*;

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
    public ResponseVO addFriendship(@RequestBody @Valid AddFriendshipReq req, @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return this.imFriendshipService.addFriendship(req);
    }

    @PostMapping("/update")
    public ResponseVO updateFriendship(@RequestBody @Valid UpdateFriendshipReq req, @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return this.imFriendshipService.updateFriendship(req);
    }

    @DeleteMapping("/delete")
    public ResponseVO deleteFriendship(@RequestBody @Valid DeleteFriendshipReq req, @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return this.imFriendshipService.deleteFriendship(req);
    }

    @DeleteMapping("/delete-all")
    public ResponseVO deleteAllFriendship(@RequestParam("user-id") String userId, @RequestParam("app-id") Integer appId) {
        return this.imFriendshipService.deleteAllFriendship(userId, appId);
    }
}
