package com.lq.im.service.friendship.service;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.friendship.model.req.*;

public interface ImFriendshipService {
    /**
     * 批量导入好友关系
     * @param req 请求(包含好友列表)
     * @return ResponseVO 导入结果
     */
    ResponseVO<?> importFriendship(ImportFriendshipReq req);

    /**
     * 添加好友关系
     * @param req 请求(包含用户id, 好友id)
     * @return ResponseVO 添加结果
     */
    ResponseVO<?> addFriendship(AddFriendshipReq req);

    /**
     * 添加好友
     * @param userId 用户id
     * @param friendInfo 好友信息
     * @param appId 应用id
     * @return 添加结果
     */
    ResponseVO<?> doInternalAddFriend(String userId, FriendInfo friendInfo, Integer appId);

    /**
     * 更新好友关系
     * @param req 请求(包含用户id, 好友关系信息)
     * @return ResponseVO
     */
    ResponseVO<?> updateFriendship(UpdateFriendshipReq req);

    /**
     * 删除好友关系
     * @param req 请求(包含用户id, 好友id)
     * @return ResponseVO
     */
    ResponseVO<?> deleteFriendship(DeleteFriendshipReq req);

    /**
     * 删除某用户的所有好友
     * @param userId 用户id
	 * @param appId 应用id
     * @return ResponseVO
     */
    ResponseVO<?> deleteAllFriendship(String userId, Integer appId);

    /**
     * 获取所有的好友关系
     * @param req 请求(包含用户id)
     * @return ResponseVO
     */
    ResponseVO<?> getAllFriendship(GetAllFriendshipReq req);

    /**
     * 获取指定的好友关系
     * @param req 请求(包含用户id, 好友id)
     * @return ResponseVO
     */
    ResponseVO<?> getFriendship(GetFriendshipReq req);

    /**
     * 批量校验好友关系
     * @param req 请求(包含用户id, 待校验好友id列表)
     * @return ResponseVO
     */
    ResponseVO<?> checkFriendship(CheckFriendshipReq req);

    /**
     * 批量导入黑名单
     * @param req 请求(包含用户id, 待拉黑用户id列表)
     * @return ResponseVO
     */
    ResponseVO<?> importBlocklist(ImportBlocklistReq req);

    /**
     * 添加黑名单
     * @param req 请求(包含用户id, 待拉黑用户id)
     * @return ResponseVO
     */
    ResponseVO<?> blockFriend(BlockFriendReq req);

    /**
     * 删除黑名单
     * @param req 请求(包含用户id, 待拉黑用户id)
     * @return ResponseVO
     */
    ResponseVO<?> unblockFriend(UnblockFriendReq req);

    /**
     * 校验黑名单
     * @param req 请求
     * @return ResponseVO
     */
    ResponseVO<?> checkBlocklist(CheckFriendshipReq req);
}
