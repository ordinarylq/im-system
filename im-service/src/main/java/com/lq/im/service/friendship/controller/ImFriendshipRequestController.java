package com.lq.im.service.friendship.controller;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.friendship.model.req.ApproveFriendRequestReq;
import com.lq.im.service.friendship.model.req.GetAllFriendshipRequestReq;
import com.lq.im.service.friendship.model.req.ReadFriendshipRequestReq;
import com.lq.im.service.friendship.service.ImFriendshipRequestService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/v1/friendship-request")
public class ImFriendshipRequestController {

    @Resource
    private ImFriendshipRequestService imFriendshipRequestService;

    @PostMapping("/approve")
    public ResponseVO<?> approveFriendshipRequest(@RequestBody @Valid ApproveFriendRequestReq req,
                                        @RequestParam("app-id") Integer appId,
                                        @RequestParam("operator") @NotBlank String operator) {
        req.setAppId(appId);
        req.setOperator(operator);
        return this.imFriendshipRequestService.approveFriendRequest(req);
    }

    @PostMapping("/read")
    public ResponseVO<?> readFriendshipRequest(@RequestBody @Valid ReadFriendshipRequestReq req,
                                            @RequestParam("app-id") Integer appId,
                                            @RequestParam("operator") @NotBlank String operator) {
        req.setAppId(appId);
        req.setOperator(operator);
        return this.imFriendshipRequestService.readFriendshipRequest(req);
    }

    @PostMapping("/get")
    public ResponseVO<?> getAllFriendshipRequest(@RequestBody @Valid GetAllFriendshipRequestReq req,
                                              @RequestParam("app-id") Integer appId,
                                              @RequestParam("operator") @NotBlank String operator) {
        req.setAppId(appId);
        req.setOperator(operator);
        return this.imFriendshipRequestService.getFriendshipRequest(req);
    }
}
