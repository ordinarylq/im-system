package com.lq.im.service.friendship.model.callback;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnBlockFriendAfterCallbackDTO {
    private String userId;
    private String friendUserId;
}
