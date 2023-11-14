package com.lq.im.service.group.controller;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.group.model.req.*;
import com.lq.im.service.group.service.ImGroupMemberService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/v1/group/member")
public class ImGroupMemberController {

    @Resource
    private ImGroupMemberService imGroupMemberService;

    @PostMapping("/import")
    public ResponseVO<?> importGroupMember(@RequestBody @Valid ImportGroupMemberReq req,
                                           @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return this.imGroupMemberService.importGroupMember(req);
    }

    @PostMapping("/invite")
    public ResponseVO<?> inviteMemberToGroup(@RequestBody @Valid InviteUserReq req,
                                             @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return this.imGroupMemberService.inviteUserIntoGroup(req);
    }

    @PostMapping("/exit")
    public ResponseVO<?> exitGroup(@RequestBody @Valid ExitGroupReq req,
                                             @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return this.imGroupMemberService.exitGroup(req);
    }

    @PostMapping("/remove")
    public ResponseVO<?> removeMemberFromGroup(@RequestBody @Valid RemoveMemberReq req,
                                             @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return this.imGroupMemberService.removeMemberFromGroup(req);
    }

    @PutMapping("/update")
    public ResponseVO<?> updateMemberInfo(@RequestBody @Valid UpdateGroupMemberReq req,
                                               @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return this.imGroupMemberService.updateGroupMemberInfo(req);
    }

    @GetMapping("/list")
    public ResponseVO<?> getGroupMemberList(@RequestParam("app-id") Integer appId,
                                            @RequestParam("group-id") @NotBlank String groupId) {
        return this.imGroupMemberService.getGroupMemberList(appId, groupId);
    }

    @PutMapping("/mute")
    public ResponseVO<?> getGroupMemberList(@RequestParam("app-id") Integer appId,
                                            @RequestBody @Valid MuteGroupMemberReq req) {
        req.setAppId(appId);
        return this.imGroupMemberService.muteGroupMember(req);
    }

}
