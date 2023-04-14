package com.lq.im.service.user.model.resp;

import com.lq.im.service.user.model.ImUserDAO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: GetUserInfoResp
 * @Author: LiQi
 * @Date: 2023-04-11 15:41
 * @Version: V1.0
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserInfoResp {

    /**
     * 获取到的用户信息列表
     */
    private List<ImUserDAO> userList = new ArrayList<>();

    /**
     * 无法获取到的用户id列表
     */
    private List<String> failUserIdList = new ArrayList<>();
}
