package com.lq.im.service.group.model.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImGroupMemberDTO {
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
     * 加入方式
     */
    private String joinType;

}
