package com.lq.im.service.user.model.resp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: AddGroupResp
 * @Author: LiQi
 * @Date: 2023-05-31 16:33
 * @Version: V1.0
 * @Description:
 */
@Data
public class AddGroupResp {
    /**
     * 插入成功的用户id
     */
    private List<String> successUserIdList = new ArrayList<>();

    /**
     * 插入失败的用户id
     */
    private List<String> failUserIdList = new ArrayList<>();
}
