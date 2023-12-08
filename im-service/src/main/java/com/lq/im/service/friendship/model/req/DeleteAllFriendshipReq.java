package com.lq.im.service.friendship.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

@Data
public class DeleteAllFriendshipReq extends RequestBase {
    private String userId;
}
