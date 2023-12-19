package com.lq.im.common.model.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class GroupMessageContent extends MessageContent{

    private String groupId;

}
