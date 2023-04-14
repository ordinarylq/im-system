package com.lq.im.service.user.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @ClassName: DeleteUserReq
 * @Author: LiQi
 * @Date: 2023-04-11 16:17
 * @Version: V1.0
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteUserReq extends RequestBase {

    @NotEmpty(message = "用户id列表不能为空")
    private List<String> userIdList;
}
