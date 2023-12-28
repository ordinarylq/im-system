package com.lq.im.message.model;

import com.lq.im.common.model.message.GroupMessageContent;
import lombok.Data;

@Data
public class GroupMessageStoreDTO {

    private GroupMessageContent groupMessageContent;

    private ImMessageBodyDAO groupMessageBodyDAO;

}
