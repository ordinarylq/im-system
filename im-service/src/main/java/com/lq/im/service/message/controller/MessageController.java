package com.lq.im.service.message.controller;

import com.lq.im.common.ResponseVO;
import com.lq.im.common.model.message.SendGroupMessageCheckReq;
import com.lq.im.common.model.message.SendMessageCheckReq;
import com.lq.im.service.message.model.req.SendGroupMessageReq;
import com.lq.im.service.message.model.req.SendPeerToPeerMessageReq;
import com.lq.im.service.message.service.GroupMessageService;
import com.lq.im.service.message.service.MessageCheckService;
import com.lq.im.service.message.service.PeerToPeerMessageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/message")
public class MessageController {

    @Resource
    private PeerToPeerMessageService peerToPeerMessageService;
    @Resource
    private GroupMessageService groupMessageService;
    @Resource
    private MessageCheckService messageCheckService;

    @PostMapping("/p2p")
    public ResponseVO<?> sendPeerToPeerMessage(@RequestBody @Valid SendPeerToPeerMessageReq req,
                                               @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return ResponseVO.successResponse(this.peerToPeerMessageService.send(req));
    }

    @PostMapping("/group")
    public ResponseVO<?> sendGroupMessage(@RequestBody @Valid SendGroupMessageReq req,
                                          @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return ResponseVO.successResponse(this.groupMessageService.send(req));
    }

    @PostMapping("/p2p/check")
    public ResponseVO<?> checkPeerToPeerMessage(@RequestBody @Valid SendMessageCheckReq req) {
        return this.messageCheckService.checkUserAndFriendship(req.getAppId(), req.getUserId(), req.getFriendUserId());
    }

    @PostMapping("/group/check")
    public ResponseVO<?> checkGroupMessage(@RequestBody @Valid SendGroupMessageCheckReq req) {
        return this.messageCheckService.canGroupMemberSendMessage(req.getAppId(), req.getUserId(), req.getGroupId());
    }

}
