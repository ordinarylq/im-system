package com.lq.im.service.group.controller;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.group.model.req.ImportGroupMemberReq;
import com.lq.im.service.group.service.ImGroupMemberService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

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
}
