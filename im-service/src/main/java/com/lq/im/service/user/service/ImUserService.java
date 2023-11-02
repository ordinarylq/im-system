package com.lq.im.service.user.service;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.user.model.ImUserDAO;
import com.lq.im.service.user.model.req.*;
import com.lq.im.service.user.model.resp.GetUserInfoResp;

public interface ImUserService {

    /**
     * 批量插入用户
     * 当要插入的用户数太多时(>100)，直接取消插入
     * @param req 请求(包含待插入用户列表)
     * @return ResponseVO 包含插入成功的用户id列表, 插入失败的用户id列表
     */
    ResponseVO<?> importUser(ImportUserReq req);

    /**
     * 批量获取用户信息
     * @param req 请求(包含用户id列表)
     * @return ResponseVO<GetUserInfoResp>
     */
    ResponseVO<GetUserInfoResp> getUserInfo(GetUserInfoReq req);

    /**
     * 获取单个用户信息
     * @param userId 用户id
	 * @param appId 应用id
     * @return ResponseVO<ImUserDAO>
     */
    ResponseVO<ImUserDAO> getSingleUserInfo(String userId , Integer appId);

    /**
     * 批量删除用户信息
     * 逻辑删除，非物理删除
     *
     * @param req 请求(包含待删除用户id列表)
     * @return ResponseVO
     */
    ResponseVO<?> deleteUser(DeleteUserReq req);


    /**
     * 修改某个用户信息
     * @param req 请求(包含用户详细信息)
     * @return ResponseVO
     */
    ResponseVO<?> modifyUserInfo(ModifyUserInfoReq req);

    /**
     * 用户登录
     * @param req
     * @return ResponseVO
     */
    ResponseVO<?> login(LoginReq req);

    ResponseVO<?> getUserSequence(GetUserSequenceReq req);
}
