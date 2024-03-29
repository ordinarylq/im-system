package com.lq.im.service.friendship.model.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddFriendshipGroupMemberResp {

    /**
     * 添加成功的用户id列表
     */
    private List<String> successUserIdList = new ArrayList<>();

    /**
     * 添加失败的结果列表
     */
    private List<ResultItem> failUserItemList = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultItem {
        private String userId;

        private String message;
    }

}
