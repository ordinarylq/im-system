package com.lq.im.service.friendship.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
