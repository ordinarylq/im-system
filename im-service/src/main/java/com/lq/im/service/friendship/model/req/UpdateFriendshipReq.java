package com.lq.im.service.friendship.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateFriendshipReq extends RequestBase {

    @NotBlank(message = "userId不能为空")
    private String userId;

    @Valid
    @NotNull(message = "friendInfo不能为空")
    private FriendInfo friendInfo;
}
