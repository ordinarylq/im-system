package com.lq.im.service.friendship.controller;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.friendship.model.req.AddFriendshipGroupReq;
import com.lq.im.service.friendship.model.req.ImportFriendshipReq;
import com.lq.im.service.friendship.model.req.RemoveFriendshipGroupReq;
import com.lq.im.service.friendship.service.ImFriendshipGroupMemberService;
import com.lq.im.service.friendship.service.ImFriendshipGroupService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @ClassName: ImFriendshipGroupController
 * @Author: LiQi
 * @Date: 2023-06-01 8:17
 * @Version: V1.0
 * @Description:
 */
@RestController
@RequestMapping("/api/v1/friendship/group")
public class ImFriendshipGroupController {

    @Resource
    private ImFriendshipGroupService imFriendshipGroupService;

    @Resource
    private ImFriendshipGroupMemberService imFriendshipGroupMemberService;

    @PostMapping("")
    public ResponseVO addFriendshipGroup(@RequestBody @Valid AddFriendshipGroupReq req,
                                         @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return this.imFriendshipGroupService.addGroup(req);
    }

    @DeleteMapping("")
    public ResponseVO removeFriendshipGroup(@RequestBody @Valid RemoveFriendshipGroupReq req,
                                            @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return this.imFriendshipGroupService.removeGroup(req);
    }

}
