package com.lq.im.service.user.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_user")
public class ImUserDAO {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名称
     */
    private String nickName;

    /**
     * 位置
     */
    private String location;

    /**
     * 生日
     */
    private String birthDay;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像地址
     */
    private String photo;

    /**
     * 性别 1-男 2-女 0-未设置/其他
     */
    private Integer userSex;

    /**
     * 个性签名
     */
    private String selfSignature;

    /**
     * 添加好友方式 1-无需验证 2-需要验证
     */
    private Integer friendAllowType;

    /**
     * 管理员禁止用户添加加好友：0-未禁用 1-已禁用
     */
    private Integer disableAddFriend;

    /**
     * 禁用标识 0-未禁用 1-已禁用
     */
    private Integer forbiddenFlag;

    /**
     * 禁言标志 0-未禁言 1-禁言
     */
    private Integer silentFlag;

    /**
     * 用户类型 1-普通用户 2-客服 3-机器人
     */
    private Integer userType;

    /**
     * 应用id
     */
    private Integer appId;

    /**
     * 删除标志 0-未删除 1-已删除
     */
    private Integer delFlag;

    /**
     * 拓展
     */
    private String extra;
}
