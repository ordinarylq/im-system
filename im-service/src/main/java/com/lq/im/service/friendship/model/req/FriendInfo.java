package com.lq.im.service.friendship.model.req;

import com.lq.im.common.enums.FriendshipStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @ClassName: FriendInfo
 * @Author: LiQi
 * @Date: 2023-04-14 9:18
 * @Version: V1.0
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendInfo {
    @NotNull(message = "friendUserId不能为空")
    private String friendUserId;

    private String remark;

    private Integer status = FriendshipStatusEnum.FRIEND_STATUS_NO_FRIEND.getCode();

    private Integer black = FriendshipStatusEnum.BLACK_STATUS_NORMAL.getCode();

    private String addSource;

    private String extra;

}