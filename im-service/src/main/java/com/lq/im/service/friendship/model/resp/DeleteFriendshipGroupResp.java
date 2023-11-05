package com.lq.im.service.friendship.model.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteFriendshipGroupResp {
    /**
     * 成功被删除的分组id列表
     */
    private List<String> successGroupNameList = new ArrayList<>();

    /**
     * 删除失败的分组id列表
     */
    private List<ResultItem> failGroupItemList = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultItem {
        /**
         * 分组id
         */
        private String groupName;

        /**
         * 失败的原因
         */
        private String message;

    }
}
