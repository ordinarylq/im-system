package com.lq.im.service.friendship.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ImportBlocklistReq extends RequestBase {

    @NotNull(message = "userId不能为空")
    private String userId;

    @NotEmpty(message = "friendUserIdList不能为空")
    private List<String> friendUserIdList;
}
