package com.lq.im.service.user.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @ClassName: GetUserSequenceReq
 * @Author: LiQi
 * @Date: 2023-04-13 13:17
 * @Version: V1.0
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserSequenceReq extends RequestBase {
    @NotNull(message = "用户ID不能为空")
    private String userId;
}
