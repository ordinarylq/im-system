package com.lq.im.service.friendship.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName: UpdateFriendshipReq
 * @Author: LiQi
 * @Date: 2023-04-14 13:55
 * @Version: V1.0
 * @Description:
 */
@Data
public class UpdateFriendshipReq extends RequestBase {

    @NotBlank(message = "userId不能为空")
    private String userId;

    @Valid
    @NotNull(message = "friendInfo不能为空")
    private FriendInfo friendInfo;
}
