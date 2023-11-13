package com.lq.im.service.group.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DismissGroupReq extends RequestBase {

    @NotBlank(message = "群组id不能为空")
    private String groupId;

}
