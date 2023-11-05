package com.lq.im.service.friendship.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GetAllFriendshipReq extends RequestBase {

    @NotBlank(message = "userId不能为空")
    private String userId;

}
