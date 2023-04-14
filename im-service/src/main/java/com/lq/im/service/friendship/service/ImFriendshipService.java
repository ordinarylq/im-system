package com.lq.im.service.friendship.service;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.friendship.model.req.AddFriendReq;
import com.lq.im.service.friendship.model.req.ImportFriendshipReq;

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
    ResponseVO addFriend(AddFriendReq req);
}
