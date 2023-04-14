package com.lq.im.service.friendship.model.req;

import com.lq.im.common.enums.FriendshipStatusEnum;
import com.lq.im.common.model.RequestBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName: ImportFriendshipReq
 * @Author: LiQi
 * @Date: 2023-04-13 16:00
 * @Version: V1.0
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportFriendshipReq extends RequestBase {

    @NotNull(message = "userId不能为空")
    private String userId;

    private List<FriendInfo> friendInfoList;

}
