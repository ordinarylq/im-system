package com.lq.im.service.group.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class CreateGroupReq extends RequestBase {

    /**
     * 群组id
     */
    private String groupId;

    /**
     * 群主用户id
     */
    private String ownerId;

    /**
     * 群组类型 1-私有群 2-公开群
     */
    private Integer groupType;

    /**
     * 群组名称
     */
    @NotBlank(message = "群组名称不能为空")
    private String groupName;

    /**
     * 是否开启群禁言 0-未开启 1-已开启
     */
    private Integer hasMute;

    /**
     * 申请加群处理方式 0-需要验证 1-自由加入 3-禁止加入
     */
    private Integer applyJoinType;

    /**
     * 群简介
     */
    private String introduction;

    /**
     * 群公告
     */
    private String notification;

    /**
     * 群组头像地址
     */
    private String photoUrl;

    /**
     * 最大群成员数量
     */
    private Integer maxMemberCount;

    /**
     * 扩展
     */
    private String extra;

    private List<ImGroupMemberDTO> memberList;
}
