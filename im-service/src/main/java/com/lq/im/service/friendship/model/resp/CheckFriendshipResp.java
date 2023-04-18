package com.lq.im.service.friendship.model.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: CheckFriendshipResp
 * @Author: LiQi
 * @Date: 2023-04-18 10:07
 * @Version: V1.0
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckFriendshipResp {

    /**
     * 应用id
     */
    private Integer appId;

    /**
     * 用户id
     */
    private String fromId;

    /**
     * 好友id
     */
    private String toId;

    /**
     * 好友关系状态 1-是好友关系 0-不是好友关系
     */
    private Integer status;
}
