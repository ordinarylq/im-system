package com.lq.im.service.user.model.req;

import com.lq.im.common.model.RequestBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName: GetUserInfoReq
 * @Author: LiQi
 * @Date: 2023-04-11 15:40
 * @Version: V1.0
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserInfoReq extends RequestBase {
    private List<String> userIdList;
}
