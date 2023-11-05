package com.lq.im.service.friendship.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_friendship_group")
public class ImFriendshipGroupDAO {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 应用id
     */
    private Integer appId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 好友分组序列号
     */
    private String sequence;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 删除标识 0-未删除 1-已删除
     */
    private Integer delFlag;
}
