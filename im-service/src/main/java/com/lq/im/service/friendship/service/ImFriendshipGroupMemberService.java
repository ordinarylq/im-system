package com.lq.im.service.friendship.service;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.friendship.model.req.AddFriendshipGroupMemberReq;
import com.lq.im.service.friendship.model.req.RemoveFriendshipGroupMemberReq;

public interface ImFriendshipGroupMemberService {

    /**
     * 添加用户到指定用户分组
     */
    Integer addGroupMember(Long groupId, String userId);

    /**
     * 清空指定用户分组的所有成员
     */
    Integer clearGroupMember(Long groupId);

    /**
     * 添加多个用户到指定用户分组
     */
    ResponseVO<?> addMultipleMembers(AddFriendshipGroupMemberReq req);

    /**
     * 删除指定用户分组的多个用户
     */
    ResponseVO<?> removeMultipleMembers(RemoveFriendshipGroupMemberReq req);
}
