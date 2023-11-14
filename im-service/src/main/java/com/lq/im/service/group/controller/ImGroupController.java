package com.lq.im.service.group.controller;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.group.model.req.*;
import com.lq.im.service.group.service.ImGroupService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/v1/group")
public class ImGroupController {
    @Resource
    ImGroupService imGroupService;

    @PostMapping("/import")
    public ResponseVO<?> importGroup(@RequestBody @Valid ImportGroupReq req, @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return this.imGroupService.importGroup(req);
    }

    @PostMapping("/create")
    public ResponseVO<?> createGroup(@RequestBody @Valid CreateGroupReq req, @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return this.imGroupService.createGroup(req);
    }

    @PutMapping("/update")
    public ResponseVO<?> updateGroupInfo(@RequestBody @Valid UpdateGroupInfoReq req,
                                         @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return this.imGroupService.updateGroupInfo(req);
    }

    @GetMapping("")
    public ResponseVO<?> getGroupWithMemberList(@RequestParam("app-id") Integer appId,
                                                @RequestParam("group-id") @NotBlank String groupId) {
        return this.imGroupService.getGroupWithMemberList(appId, groupId);
    }

    @PostMapping("/joined")
    public ResponseVO<?> getJoinedGroupInfoList(@RequestParam("app-id") Integer appId,
                                                @RequestBody @Valid GetJoinedGroupListReq req) {
        req.setAppId(appId);
        return this.imGroupService.getJoinedGroupList(req);
    }

    @PostMapping("/dismiss")
    public ResponseVO<?> dismissGroupList(@RequestParam("app-id") Integer appId,
                                                @RequestBody @Valid DismissGroupReq req) {
        req.setAppId(appId);
        return this.imGroupService.dismissGroup(req);
    }

    @PostMapping("/hand-over")
    public ResponseVO<?> handOverGroupList(@RequestParam("app-id") Integer appId,
                                          @RequestBody @Valid HandOverGroupReq req) {
        req.setAppId(appId);
        return this.imGroupService.handOverGroup(req);
    }

    @PostMapping("/mute")
    public ResponseVO<?> muteGroup(@RequestParam("app-id") Integer appId,
                                           @RequestBody @Valid MuteGroupReq req) {
        req.setAppId(appId);
        return this.imGroupService.muteGroup(req);
    }
}
