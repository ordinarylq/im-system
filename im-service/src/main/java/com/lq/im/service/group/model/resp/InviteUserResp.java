package com.lq.im.service.group.model.resp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InviteUserResp {
    private List<String> successUserIdList = new ArrayList<>();

    private List<ImportGroupMemberResp.ResultItem> failMemberItemList = new ArrayList<>();

}
