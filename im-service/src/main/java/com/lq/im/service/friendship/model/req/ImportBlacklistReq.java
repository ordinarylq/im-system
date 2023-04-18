package com.lq.im.service.friendship.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName: ImportBlacklistReq
 * @Author: LiQi
 * @Date: 2023-04-18 13:02
 * @Version: V1.0
 * @Description:
 */
@Data
public class ImportBlacklistReq extends RequestBase {

    @NotNull(message = "userId不能为空")
    private String userId;

    @NotEmpty(message = "friendUserIdList不能为空")
    private List<String> friendUserIdList;
}
