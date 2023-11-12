package com.lq.im.service.group.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ImportGroupMemberReq extends RequestBase {
    @NotBlank(message = "群组id不能为空")
    private String groupId;

    @NotEmpty
    private List<ImGroupMemberDTO> memberList;
}
