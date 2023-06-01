package com.lq.im.service.friendship.service;

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

}
