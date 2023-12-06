package com.lq.im.service.group.model.callback;

import com.lq.im.service.group.model.resp.InviteUserResp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AfterAddChatGroupMemberCallbackDTO {
    private String groupId;
    private Integer groupType;
    private String operator;
    private InviteUserResp resp;
}
