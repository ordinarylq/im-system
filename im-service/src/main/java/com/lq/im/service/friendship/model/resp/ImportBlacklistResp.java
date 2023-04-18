package com.lq.im.service.friendship.model.resp;

import com.lq.im.common.exception.ApplicationExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ImportBlacklistResp
 * @Author: LiQi
 * @Date: 2023-04-18 13:09
 * @Version: V1.0
 * @Description:
 */
@Data
public class ImportBlacklistResp {

    /**
     * 添加黑名单的列表及失败原因
     */
    private List<ResultItem> resultList = new ArrayList<>();

    /**
     * 添加黑名单失败的id列表
     */
    private List<String> failList = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultItem {
        private String userId;
        private Integer resultCode;
        private String resultMessage;

        public void setCodeAndMessage(ApplicationExceptionEnum exceptionEnum) {
            this.resultCode = exceptionEnum.getCode();
            this.resultMessage = exceptionEnum.getError();
        }
    }
}
