package com.lq.im.service.group.model.message;

import lombok.Data;

@Data
public class UpdateGroupInfoDTO {
    private Integer appId;
    private String groupId;
    private String groupName;
    private Integer hasMute;
    private Integer applyJoinType;
    private String introduction;
    private String notification;
    private String photoUrl;
    private Integer maxMemberCount;
    private String extra;
    private Long sequence;
}
