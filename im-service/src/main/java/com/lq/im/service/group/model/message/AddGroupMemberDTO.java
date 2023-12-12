package com.lq.im.service.group.model.message;

import lombok.Data;

import java.util.List;

@Data
public class AddGroupMemberDTO {
    private Integer appId;

    private String groupId;

    private List<String> inviteeIdList;
}
