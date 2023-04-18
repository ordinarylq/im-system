package com.lq.im.service.friendship.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName: DeleteBlackReq
 * @Author: LiQi
 * @Date: 2023-04-18 14:36
 * @Version: V1.0
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteBlackReq extends RequestBase {

    @NotBlank(message = "userId不能为空")
    private String userId;

    @NotBlank(message = "friendUserId不能为空")
    private String friendUserId;
}
