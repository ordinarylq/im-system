package com.lq.im.service.friendship.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ImFriendshipRequestDAO
 * @Author: LiQi
 * @Date: 2023-04-27 16:42
 * @Version: V1.0
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_friendship_request")
public class ImFriendshipRequestDAO {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 应用id
     */
    private Integer appId;

    /**
     * 用户id
     */
    @TableField("from_id")
    private String userId;

    /**
     * 申请人id
     */
    @TableField("to_id")
    private String friendId;

    /**
     * 是否已读 0-未读 1-已读
     */
    private Integer readStatus;

    /**
     * 好友申请附加信息
     */
    private String addWording;

    /**
     * 备注
     */
    private String remark;

    /**
     * 用户审批状态
     */
    private Integer approveStatus;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    private Long sequence;

    /**
     * 添加来源
     */
    private String addSource;
}