package com.lq.im.service.friendship.service;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.friendship.model.req.AddFriendshipReq;
import com.lq.im.service.friendship.model.req.DeleteFriendshipReq;
import com.lq.im.service.friendship.model.req.ImportFriendshipReq;
import com.lq.im.service.friendship.model.req.UpdateFriendshipReq;

/**
 * @ClassName: ImFriendshipService
 * @Author: LiQi
 * @Date: 2023-04-13 15:19
 * @Version: V1.0
 */
public interface ImFriendshipService {
    /**
     * 批量导入好友关系
     * @author LiQi
     * @param req 请求(包含好友列表)
     * @return ResponseVO
     */
    ResponseVO importFriendship(ImportFriendshipReq req);

    /**
     * 添加好友关系
     * @author LiQi
     * @param req 请求(包含用户id, 好友id)
     * @return ResponseVO
     */
    ResponseVO addFriendship(AddFriendshipReq req);


    /**
     * 更新好友关系
     * @author LiQi
     * @param req 请求(包含用户id, 好友关系信息)
     * @return ResponseVO
     */
    ResponseVO updateFriendship(UpdateFriendshipReq req);

    /**
     * 删除好友关系
     * @author LiQi
     * @param req 请求(包含用户id, 好友id)
     * @return ResponseVO
     */
    ResponseVO deleteFriendship(DeleteFriendshipReq req);

    /**
     * 删除某用户的所有好友
     * @author LiQi
     * @param userId 用户id
	 * @param appId 应用id
     * @return ResponseVO
     */
    ResponseVO deleteAllFriendship(String userId, Integer appId);
}
