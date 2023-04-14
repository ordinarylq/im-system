package com.lq.im.service.friendship.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName: GetAllFriendshipReq
 * @Author: LiQi
 * @Date: 2023-04-14 15:11
 * @Version: V1.0
 * @Description:
 */
@Data
public class GetAllFriendshipReq extends RequestBase {

    @NotBlank(message = "userId不能为空")
    private String userId;

}
