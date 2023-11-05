package com.lq.im.service.friendship.model.req;

import com.lq.im.common.enums.FriendshipStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendInfo {
    @NotBlank(message = "friendUserId不能为空")
    private String friendUserId;

    private String remark;

    private Integer status = FriendshipStatusEnum.FRIEND_STATUS_NO_FRIEND.getCode();

    private Integer black = FriendshipStatusEnum.BLOCK_STATUS_NORMAL.getCode();

    private String addSource;

    private String extra;

    private String addWording;
}