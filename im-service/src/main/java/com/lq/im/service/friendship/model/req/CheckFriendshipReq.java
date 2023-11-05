package com.lq.im.service.friendship.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CheckFriendshipReq extends RequestBase {

    @NotBlank(message = "userId不能为空")
    private String userId;

    @NotEmpty(message = "friendIdList不能为空")
    private List<String> friendIdList;

    @NotNull(message = "checkType不能为空")
    private Integer checkType;
}
