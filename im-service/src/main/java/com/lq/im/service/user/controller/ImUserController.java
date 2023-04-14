package com.lq.im.service.user.controller;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.user.model.req.*;
import com.lq.im.service.user.service.ImUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @ClassName: ImUserController
 * @Author: LiQi
 * @Date: 2023-04-11 14:33
 * @Version: V1.0
 * @Description:
 */
@RestController
@RequestMapping("/api/v1/user")
public class ImUserController {

    @Resource
    private ImUserService imUserService;

    @PostMapping("/import")
    public ResponseVO importUser(@RequestBody ImportUserReq req, @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return this.imUserService.importUser(req);
    }

    @PostMapping("/get")
    public ResponseVO getUser(@RequestBody GetUserInfoReq req, @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return this.imUserService.getUserInfo(req);
    }

    @GetMapping("/get")
    public ResponseVO getSingleUser(@RequestParam("user-id") String userId, @RequestParam("app-id") Integer appId) {
        return this.imUserService.getSingleUserInfo(userId, appId);
    }

    @DeleteMapping("/delete")
    public ResponseVO deleteUser(@RequestBody DeleteUserReq req, @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return this.imUserService.deleteUser(req);
    }

    @PutMapping("/modify")
    public ResponseVO modifyUser(@RequestBody ModifyUserInfoReq req, @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        return this.imUserService.modifyUserInfo(req);
    }

    @RequestMapping("/login")
    public ResponseVO login(@RequestBody LoginReq req, @RequestParam("app-id") Integer appId) {
        req.setAppId(appId);
        // todo 校验登录逻辑
        return this.imUserService.login(req);
    }
}
