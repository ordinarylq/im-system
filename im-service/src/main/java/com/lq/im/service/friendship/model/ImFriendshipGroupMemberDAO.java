package com.lq.im.service.friendship.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 好友分组成员
 * @ClassName: ImFriendshipGroupMemberDAO
 * @Author: LiQi
 * @Date: 2023-05-31 15:45
 * @Version: V1.0
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_friendship_group_member")
public class ImFriendshipGroupMemberDAO {

    /**
     * 好友分组id
     */
    private Long groupId;

    /**
     * 好友id
     */
    private String userId;
}
