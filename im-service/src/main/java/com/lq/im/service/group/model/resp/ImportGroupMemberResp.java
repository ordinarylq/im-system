package com.lq.im.service.group.model.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImportGroupMemberResp {
    private List<String> successMemberIdList = new ArrayList<>();

    private List<ResultItem> failMemberItemList = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultItem {
        private String memberId;

        private String message;
    }
}
