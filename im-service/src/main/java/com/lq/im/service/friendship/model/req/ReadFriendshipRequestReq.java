package com.lq.im.service.friendship.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName: ReadFriendshipRequestReq
 * @Author: LiQi
 * @Date: 2023-05-31 14:29
 * @Version: V1.0
 * @Description:
 */
@Data
public class ReadFriendshipRequestReq extends RequestBase {

    @NotBlank(message = "userId不能为空")
    private String userId;
}
