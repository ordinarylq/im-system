package com.lq.im.service.user.model.req;

import com.lq.im.common.enums.gateway.LoginClientType;
import com.lq.im.common.model.RequestBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginReq extends RequestBase {
    @NotNull(message = "用户id不能位空")
    private String userId;

    /**
     * 客户端类型
     * @see LoginClientType
     */
    private Integer clientType;
}
