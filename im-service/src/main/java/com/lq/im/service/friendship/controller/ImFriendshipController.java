package com.lq.im.service.friendship.controller;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.friendship.model.req.*;
import com.lq.im.service.friendship.service.ImFriendshipService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

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
    public ResponseVO importFriendship(@RequestBody @Valid ImportFriendshipReq req, @RequestParam("app-id") @NotBlank Integer appId) {
        req.setAppId(appId);
        return this.imFriendshipService.importFriendship(req);
    }

    @PostMapping("/add")
    public ResponseVO addFriendship(@RequestBody @Valid AddFriendshipReq req,
                                    @RequestParam("app-id") @NotBlank Integer appId) {
        req.setAppId(appId);
        return this.imFriendshipService.addFriendship(req);
    }

    @PostMapping("/update")
    public ResponseVO updateFriendship(@RequestBody @Valid UpdateFriendshipReq req,
                                       @RequestParam("app-id") @NotBlank Integer appId) {
        req.setAppId(appId);
        return this.imFriendshipService.updateFriendship(req);
    }

    @DeleteMapping("/delete")
    public ResponseVO deleteFriendship(@RequestBody @Valid DeleteFriendshipReq req,
                                       @RequestParam("app-id") @NotBlank Integer appId) {
        req.setAppId(appId);
        return this.imFriendshipService.deleteFriendship(req);
    }

    @DeleteMapping("/delete-all")
    public ResponseVO deleteAllFriendship(@RequestParam("user-id") @NotBlank String userId,
                                          @RequestParam("app-id") @NotBlank Integer appId) {
        return this.imFriendshipService.deleteAllFriendship(userId, appId);
    }

    @PostMapping("/get")
    public ResponseVO getFriendship(@RequestBody @Valid GetFriendshipReq req,
                                    @RequestParam("app-id") @NotBlank Integer appId) {
        req.setAppId(appId);
        return this.imFriendshipService.getFriendship(req);
    }

    @PostMapping("/get-all")
    public ResponseVO getAllFriendship(@RequestBody @Valid GetAllFriendshipReq req,
                                       @RequestParam("app-id") @NotBlank Integer appId) {
        req.setAppId(appId);
        return this.imFriendshipService.getAllFriendship(req);
    }


}
