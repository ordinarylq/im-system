package com.lq.im.service.user.model.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @ClassName: LoginReq
 * @Author: LiQi
 * @Date: 2023-04-13 13:10
 * @Version: V1.0
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginReq {

    @NotNull(message = "用户id不能位空")
    private String userId;

    @NotNull(message = "appId不能为空")
    private Integer appId;

    private Integer clientType;
}
