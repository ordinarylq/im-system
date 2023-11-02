package com.lq.im.service.friendship.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddFriendshipReq extends RequestBase {

    @NotBlank(message = "userId不能为空")
    private String userId;

    @Valid
    @NotNull(message = "friendInfo不能为空")
    private FriendInfo friendInfo;
}
