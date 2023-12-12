package com.lq.im.service.group.model.message;

import lombok.Data;

@Data
public class DismissGroupDTO {
    private Integer appId;
    private String groupId;
    private Long sequence;
}
