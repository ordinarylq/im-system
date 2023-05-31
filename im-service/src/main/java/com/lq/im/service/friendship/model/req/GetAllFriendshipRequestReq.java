package com.lq.im.service.friendship.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName: GetAllFriendshipRequestReq
 * @Author: LiQi
 * @Date: 2023-05-31 14:43
 * @Version: V1.0
 * @Description:
 */
@Data
public class GetAllFriendshipRequestReq extends RequestBase {

    @NotNull(message = "userId不能为空")
    private String userId;
}
