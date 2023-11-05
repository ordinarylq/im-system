package com.lq.im.service.friendship.model.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoveFriendshipGroupMemberResp {
    /**
     * 成功被删除的用户id列表
     */
    private List<String> successUserIdList = new ArrayList<>();

    /**
     * 删除失败的用户id列表
     */
    private List<ResultItem> failUserItemList = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultItem {
        /**
         * 用户id
         */
        private String userId;

        /**
         * 失败的原因
         */
        private String message;

    }

}
