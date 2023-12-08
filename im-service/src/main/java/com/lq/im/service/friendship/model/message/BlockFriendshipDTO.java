package com.lq.im.service.friendship.model.message;

import lombok.Data;

@Data
public class BlockFriendshipDTO {
    private Integer appId;
    private String userId;
    private String friendshipUserId;
    private Long sequence;
}
