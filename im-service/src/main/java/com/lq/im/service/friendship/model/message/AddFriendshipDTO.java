package com.lq.im.service.friendship.model.message;

import lombok.Data;

@Data
public class AddFriendshipDTO {
    private Integer appId;
    private String userId;
    private String friendUserId;
    private String remark;
    private String addSource;
    private String addWording;
    private Long sequence;
}
