package com.lq.im.service.friendship.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName: GetFriendshipReq
 * @Author: LiQi
 * @Date: 2023-04-14 15:12
 * @Version: V1.0
 * @Description:
 */
@Data
public class GetFriendshipReq extends RequestBase {

    @NotBlank(message = "userId不能为空")
    private String userId;

    @NotBlank(message = "friendUserId不能为空")
    private String friendUserId;
}
