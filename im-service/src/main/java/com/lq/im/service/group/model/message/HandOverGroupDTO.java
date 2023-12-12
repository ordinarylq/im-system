package com.lq.im.service.group.model.message;

import lombok.Data;

@Data
public class HandOverGroupDTO {
    private Integer appId;
    private String groupId;
    private String assigneeId;
}
