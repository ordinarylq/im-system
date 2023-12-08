package com.lq.im.service.friendship.model.message;

import lombok.Data;

@Data
public class AddFriendshipGroupDTO {
    private Integer appId;
    private String userId;
    private String groupName;
    private Long sequence;
}
