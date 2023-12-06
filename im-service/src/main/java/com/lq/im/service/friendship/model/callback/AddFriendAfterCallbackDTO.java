package com.lq.im.service.friendship.model.callback;

import com.lq.im.service.friendship.model.req.FriendInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddFriendAfterCallbackDTO {
    private String userId;
    private FriendInfo friendInfo;
}
