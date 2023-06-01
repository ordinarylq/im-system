package com.lq.im.service.friendship.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @ClassName: RemoveFriendshipGroupReq
 * @Author: LiQi
 * @Date: 2023-05-31 15:54
 * @Version: V1.0
 * @Description:
 */
@Data
public class RemoveFriendshipGroupReq extends RequestBase {

    @NotBlank(message = "userId不能为空")
    private String userId;

    @NotEmpty(message = "分组名称不能为空")
    private List<String> groupNameList;
}
