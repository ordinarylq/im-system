package com.lq.im.service.user.model.resp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
