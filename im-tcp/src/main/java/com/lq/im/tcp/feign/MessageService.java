package com.lq.im.tcp.feign;

import com.lq.im.common.ResponseVO;
import com.lq.im.common.model.message.SendGroupMessageCheckReq;
import com.lq.im.common.model.message.SendMessageCheckReq;
import feign.Headers;
import feign.RequestLine;

public interface MessageService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @RequestLine("POST /message/p2p/check")
    ResponseVO<?> checkPeerToPeerMessage(SendMessageCheckReq req);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @RequestLine("POST /message/group/check")
    ResponseVO<?> checkGroupMessage(SendGroupMessageCheckReq req);

}
