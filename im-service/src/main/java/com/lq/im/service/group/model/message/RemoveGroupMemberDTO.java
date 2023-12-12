package com.lq.im.service.group.model.message;

import lombok.Data;

@Data
public class RemoveGroupMemberDTO {
    private Integer appId;
    private String groupId;
    private String memberId;
}
