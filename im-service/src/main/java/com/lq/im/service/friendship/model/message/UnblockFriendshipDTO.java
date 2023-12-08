package com.lq.im.service.friendship.model.message;

import lombok.Data;

@Data
public class UnblockFriendshipDTO {
    private Integer appId;
    private String userId;
    private String friendUserId;
    private Long sequence;
}
