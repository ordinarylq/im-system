package com.lq.im.service.user.model.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

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
