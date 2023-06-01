package com.lq.im.service.group.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
*@ClassName: ImGroupMemberDAO
*@Author: LiQi
*@Date: 2023-06-01 14:18
*@Version: V1.0
*@Description:
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_group_member")
public class ImGroupMemberDAO {

    /**
     * 主键id
     */
    @TableId
    private Long id;
    
    /**
     * 应用id
     */
    private Integer appId;

    /**
     * 群组id
     */
    private String groupId;
    
    /**
     * 成员用户Id
     */
    private String memberId;
    
    /**
     * 禁言到期日期
     */
    private Long speakDate;

    /**
     * 群成员类型 0-普通成员 1-管理员 2-群主 3-已退出
     */
    private Integer memberRole;

    /**
     * 群昵称
     */
    private String alias;

    /**
     * 加入时间
     */
    private Long joinTime;
    
    /**
     * 离开时间
     */
    private Long leaveTime;
    
    /**
     * 加入方式
     */
    private String joinType;

    /**
     * 扩展
     */
    private String extra;
}
