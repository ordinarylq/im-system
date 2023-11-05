package com.lq.im.service.friendship.service;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.friendship.model.ImFriendshipGroupDAO;
import com.lq.im.service.friendship.model.req.AddFriendshipGroupReq;
import com.lq.im.service.friendship.model.req.RemoveFriendshipGroupReq;

public interface ImFriendshipGroupService {

    /**
     * 添加好友分组
     * 支持添加分组时添加分组包含的好友
     * @param req 请求
     * @return ResponseVO
     */
    ResponseVO<?> addGroup(AddFriendshipGroupReq req);


    /**
     * 删除好友分组
     * @param req 请求
     * @return ResponseVO
     */
    ResponseVO<?> removeGroup(RemoveFriendshipGroupReq req);

    /**
     * 获取指定名称的分组
     * @param appId 应用id
	 * @param userId 用户id
	 * @param groupName 分组名称
     * @return ResponseVO 分组详情
     */
    ResponseVO<ImFriendshipGroupDAO> getGroup(Integer appId, String userId, String groupName);

    /**
     * 更新指定名称的分组信息
     * @param appId 应用id
	 * @param userId 用户id
	 * @param groupName 分组名称
     * @return Long 1-更新成功 0-更新失败
     */
    Long modifyGroup(Integer appId, String userId, String groupName);
}
