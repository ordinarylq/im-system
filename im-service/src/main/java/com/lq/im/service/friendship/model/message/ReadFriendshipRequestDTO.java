package com.lq.im.service.friendship.model.message;

import lombok.Data;

@Data
public class ReadFriendshipRequestDTO {
    private Integer appId;
    private String userId;
    private Long sequence;
}
