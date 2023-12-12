package com.lq.im.service.group.model.message;

import lombok.Data;

@Data
public class CreateGroupDTO {
    private Integer appId;
    private String groupId;
    private String ownerId;
    private Integer groupType;
    private String groupName;
    private Integer hasMute;
    private Integer applyJoinType;
    private String introduction;
    private String notification;
    private String photoUrl;
    private Integer status;
    private Long createTime;
    private String extra;
    private Long sequence;
}
