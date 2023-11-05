package com.lq.im.service.friendship.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GetAllFriendshipRequestReq extends RequestBase {

    @NotNull(message = "userId不能为空")
    private String userId;
}
