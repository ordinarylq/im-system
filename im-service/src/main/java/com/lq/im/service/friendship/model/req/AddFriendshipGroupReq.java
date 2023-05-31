package com.lq.im.service.friendship.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @ClassName: AddFriendshipGroupReq
 * @Author: LiQi
 * @Date: 2023-05-31 15:51
 * @Version: V1.0
 * @Description:
 */
@Data
public class AddFriendshipGroupReq extends RequestBase {

    @NotBlank(message = "userId不能为空")
    private String userId;

    @NotBlank(message = "分组名称不能为空")
    private String groupName;

    private List<String> friendIdList;
}
