package com.lq.im.service.friendship.service;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.friendship.model.req.AddFriendshipGroupMemberReq;
import com.lq.im.service.friendship.model.req.RemoveFriendshipGroupMemberReq;

public interface ImFriendshipGroupMemberService {

    /**
     * 添加用户到指定用户分组
     * @param groupId 分组id
	 * @param userId 用户id
     * @return Integer
     */
    Integer addGroupMember(Long groupId, String userId);

    /**
     * 清空指定用户分组的成员
     * @param groupId 用户分组id
     * @return Integer 删除的成员的个数
     */
    Integer clearGroupMember(Long groupId);

    /**
     * 添加多个用户到指定分组中
     * @param req 请求(包括多个用户id)
     * @return ResponseVO
     */
    ResponseVO<?> addMultipleMembers(AddFriendshipGroupMemberReq req);


    /**
     * 删除指定分组的多个用户
     * @param req 请求(包含多个用户id)
     * @return ResponseVO
     */
    ResponseVO<?> removeMultipleMembers(RemoveFriendshipGroupMemberReq req);
}
