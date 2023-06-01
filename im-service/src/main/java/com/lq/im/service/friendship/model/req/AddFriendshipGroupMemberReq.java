package com.lq.im.service.friendship.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @ClassName: AddFriendshipGroupMemberReq
 * @Author: LiQi
 * @Date: 2023-06-01 8:47
 * @Version: V1.0
 * @Description:
 */
@Data
public class AddFriendshipGroupMemberReq extends RequestBase {

    @NotBlank(message = "userId不能为空")
    private String userId;

    @NotBlank(message = "分组名称不能为空")
    private String groupName;

    @NotEmpty(message = "分组成员不能为空")
    private List<String> friendIdList;
}
