package com.lq.im.service.user.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModifyUserInfoReq extends RequestBase {
    // 用户id
    @NotEmpty(message = "用户id不能为空")
    private String userId;

    // 用户名称
    private String nickName;

    // 位置
    private String location;

    // 生日
    private String birthDay;

    private String password;

    // 头像
    private String photo;

    // 性别 1-男 2-女 0-未设置/其他
    private Integer userSex;

    // 个性签名
    private String selfSignature;

    // 添加好友方式 1-无需验证 2-需要验证
    private Integer friendAllowType;

    private String extra;
}
