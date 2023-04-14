package com.lq.im.service.friendship.model.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ImportFriendshipResp
 * @Author: LiQi
 * @Date: 2023-04-13 16:21
 * @Version: V1.0
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportFriendshipResp {

    /**
     * 添加好友成功的好友id列表
     */
    private List<String> successFriendIdList = new ArrayList<>();

    /**
     * 添加好友失败的好友id列表
     */
    private List<String> failFriendIdList = new ArrayList<>();
}
