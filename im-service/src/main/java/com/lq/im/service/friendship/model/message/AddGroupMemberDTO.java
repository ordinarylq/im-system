package com.lq.im.service.friendship.model.message;

import lombok.Data;

import java.util.List;

@Data
public class AddGroupMemberDTO {
    private Integer appId;
    private String userId;
    private String groupName;
    private List<String> friendIdList;
    private Long sequence;
}
