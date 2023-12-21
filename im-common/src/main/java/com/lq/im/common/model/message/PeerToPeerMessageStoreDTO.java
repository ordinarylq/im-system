package com.lq.im.common.model.message;

import lombok.Data;

@Data
public class PeerToPeerMessageStoreDTO {

    private MessageContent messageContent;

    private PeerToPeerMessageBodyDTO messageBody;

}
