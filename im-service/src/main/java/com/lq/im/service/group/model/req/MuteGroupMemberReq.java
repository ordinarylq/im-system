package com.lq.im.service.group.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MuteGroupMemberReq extends RequestBase {

    @NotBlank(message = "群组id不能为空")
    private String groupId;

    @NotBlank(message = "被禁言人用户id不能为空")
    private String memberId;

    @NotNull
    private Long speakDate;
}
