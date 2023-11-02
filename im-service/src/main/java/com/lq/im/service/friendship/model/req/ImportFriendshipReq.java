package com.lq.im.service.friendship.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportFriendshipReq extends RequestBase {

    @NotBlank(message = "userId不能为空")
    private String userId;

    private List<FriendInfo> friendInfoList;
}
