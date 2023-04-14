package com.lq.im.service.user.model.req;

import com.lq.im.common.model.RequestBase;
import com.lq.im.service.user.model.ImUserDAO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName: ImportUserReq
 * @Author: LiQi
 * @Date: 2023-04-11 14:15
 * @Version: V1.0
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportUserReq extends RequestBase {

    private List<ImUserDAO> userList;
}
