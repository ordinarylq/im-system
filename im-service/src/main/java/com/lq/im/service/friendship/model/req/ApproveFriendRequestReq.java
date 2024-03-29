package com.lq.im.service.friendship.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApproveFriendRequestReq extends RequestBase {

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户审批状态 1-同意 2-拒绝
     */
    private Integer approveStatus;
}
