package com.lq.im.service.group.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class GetJoinedGroupListReq extends RequestBase {

    @NotBlank(message = "userId不能为空")
    private String userId;

    private List<Integer> groupTypeList;

}
