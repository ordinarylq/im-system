package com.lq.im.service.friendship.service;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.friendship.model.req.ApproveFriendRequestReq;
import com.lq.im.service.friendship.model.req.FriendInfo;
import com.lq.im.service.friendship.model.req.GetAllFriendshipRequestReq;
import com.lq.im.service.friendship.model.req.ReadFriendshipRequestReq;

public interface ImFriendshipRequestService {

    /**
     * 添加好友申请
     * @param appId 应用id
	 * @param userId 申请人id
	 * @param friendInfo 好友信息
     * @return ResponseVO
     */
    ResponseVO<?> addFriendRequest(Integer appId, String userId, FriendInfo friendInfo);

    /**
     * 审批好友记录
     * @param req 审批请求(包含审批结果)
     * @return ResponseVO 响应
     */
    ResponseVO<?> approveFriendRequest(ApproveFriendRequestReq req);

    /**
     * 设置所有好友申请为已读
     * @param req 已读请求
     * @return ResponseVO
     */
    ResponseVO<?> readFriendshipRequest(ReadFriendshipRequestReq req);


    /**
     * 获取某用户的所有好友申请
     * @param req 请求信息
     * @return ResponseVO
     */
    ResponseVO<?> getFriendshipRequest(GetAllFriendshipRequestReq req);
}
