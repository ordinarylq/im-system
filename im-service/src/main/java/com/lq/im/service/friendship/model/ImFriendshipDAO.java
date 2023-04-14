package com.lq.im.service.friendship.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ImFriendshipDAO
 * @Author: LiQi
 * @Date: 2023-04-13 15:49
 * @Version: V1.0
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_friendship")
public class ImFriendshipDAO {

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
     * 好友用户id
     */
    @TableField("to_id")
    private String friendUserId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 好友关系状态 1-正常 2-已删除 0-未添加好友
     */
    private Integer status;

    /**
     * 用户是否拉黑好友 1-正常 0-已拉黑
     */
    private Integer black;

    /**
     * 黑名单关系序列号
     */
    private Long blackSequence;

    /**
     * 好友关系创建时间
     */
    private Long createTime;

    /**
     * 好友关系序列号
     */
    private Long friendSequence;

    /**
     * 添加好友来源
     */
    private String addSource;

    /**
     * 拓展
     */
    private String extra;


}
