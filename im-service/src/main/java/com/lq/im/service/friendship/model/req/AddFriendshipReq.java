package com.lq.im.service.friendship.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @ClassName: AddFriendshipReq
 * @Author: LiQi
 * @Date: 2023-04-14 8:49
 * @Version: V1.0
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddFriendshipReq extends RequestBase {
    @NotNull(message = "userId不能为空")
    private String userId;

    @Valid
    @NotNull(message = "friendInfo不能为空")
    private FriendInfo friendInfo;
}
