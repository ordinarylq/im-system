package com.lq.im.service.friendship.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lq.im.service.friendship.model.ImFriendshipDAO;
import com.lq.im.service.friendship.model.req.CheckFriendshipReq;
import com.lq.im.service.friendship.model.resp.CheckFriendshipResp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName: ImFriendshipMapper
 * @Author: LiQi
 * @Date: 2023-04-13 15:58
 * @Version: V1.0
 */
@Mapper
public interface ImFriendshipMapper extends BaseMapper<ImFriendshipDAO> {

    /**
     * 批量校验好友关系
     * 单向校验
     */
    List<CheckFriendshipResp> singleCheckFriendshipStatus(CheckFriendshipReq req);

    /**
     * 批量校验好友关系
     * 双向校验
     */
    List<CheckFriendshipResp> bothCheckFriendshipStatus(CheckFriendshipReq req);

    /**
     * 批量校验黑名单 单向校验
     */
    List<CheckFriendshipResp> singleCheckBlacklist(CheckFriendshipReq req);

    /**
     * 批量校验黑名单 双向校验
     */
    List<CheckFriendshipResp> bothCheckBlacklist(CheckFriendshipReq req);

}
