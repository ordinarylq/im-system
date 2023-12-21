package com.lq.im.message.model;

import com.lq.im.common.model.message.MessageContent;
import lombok.Data;

@Data
public class PeerToPeerMessageStoreDTO {

    private MessageContent messageContent;

    private ImMessageBodyDAO messageBodyDAO;

}
