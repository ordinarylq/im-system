package com.lq.im.service.friendship.service;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.friendship.model.req.ApproveFriendRequestReq;
import com.lq.im.service.friendship.model.req.FriendInfo;

/**
 * @ClassName: ImFriendshipRequestService
 * @Author: LiQi
 * @Date: 2023-04-28 8:43
 * @Version: V1.0
 */
public interface ImFriendshipRequestService {

    /**
     * 添加好友申请
     * @author LiQi
     * @param appId 应用id
	 * @param userId 申请人id
	 * @param friendInfo 好友信息
     * @return ResponseVO
     */
    ResponseVO addFriendRequest(Integer appId, String userId, FriendInfo friendInfo);

    ResponseVO approveFriendRequest(ApproveFriendRequestReq req);
}
