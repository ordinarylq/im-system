package com.lq.im.service.friendship.service;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.friendship.model.req.AddFriendshipGroupMemberReq;

/**
 * @ClassName: ImFriendshipGroupMemberService
 * @Author: LiQi
 * @Date: 2023-05-31 16:28
 * @Version: V1.0
 */
public interface ImFriendshipGroupMemberService {

    /**
     * 添加用户到指定用户分组
     * @author LiQi
     * @param groupId 分组id
	 * @param userId 用户id
     * @return Integer
     */
    Integer addGroupMember(Long groupId, String userId);

    /**
     * 清空指定用户分组的成员
     * @author LiQi
     * @param groupId 用户分组id
     * @return Integer 删除的成员的个数
     */
    Integer clearGroupMember(Long groupId);

    /**
     * 添加多个用户到指定分组中
     * @author LiQi
     * @param req 请求(包括多个用户id)
     * @return ResponseVO
     */
    ResponseVO addMultipleMembers(AddFriendshipGroupMemberReq req);
}
