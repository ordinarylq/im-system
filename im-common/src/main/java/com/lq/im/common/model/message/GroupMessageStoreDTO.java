package com.lq.im.common.model.message;

import lombok.Data;

@Data
public class GroupMessageStoreDTO {

    private GroupMessageContent groupMessageContent;

    private MessageBodyDTO groupMessageBody;

}
