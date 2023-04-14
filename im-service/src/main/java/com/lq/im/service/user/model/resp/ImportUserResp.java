package com.lq.im.service.user.model.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ImportUserResp
 * @Author: LiQi
 * @Date: 2023-04-11 14:22
 * @Version: V1.0
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportUserResp {

    /**
     * 插入成功的用户id
     */
    private List<String> successUserIdList = new ArrayList<>();

    /**
     * 插入失败的用户id
     */
    private List<String> failUserIdList = new ArrayList<>();
}
