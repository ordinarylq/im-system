package com.lq.im.service.friendship.model.message;

import lombok.Data;

@Data
public class ApproveFriendshipRequestDTO {
    private Integer appId;
    private Long id;
    private Integer approveStatus;
    private Long sequence;
}
