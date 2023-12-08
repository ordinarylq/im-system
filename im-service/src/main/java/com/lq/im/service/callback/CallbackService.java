package com.lq.im.service.callback;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.config.HttpClientProperties;
import com.lq.im.service.utils.HttpRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CallbackService {

    @Resource
    private HttpRequestUtils httpRequestUtils;

    @Resource
    private HttpClientProperties httpClientProperties;

    public ResponseVO<?> beforeCallback(Integer appId, String callbackCommand, String jsonBody) {
        Map<String, Object> params = getRequestParamMap(appId, callbackCommand);
        try {
            return this.httpRequestUtils.doPost(this.httpClientProperties.getCallbackUrl(), ResponseVO.class, params, jsonBody, null);
        } catch (URISyntaxException e) {
            log.error(String.format("Before callback: appid-[%d] command-[%s], exception：%s", appId, callbackCommand, e.getMessage()));
            return ResponseVO.successResponse();
        }
    }

    public void afterCallback(Integer appId, String callbackCommand, String jsonBody) {
        Map<String, Object> params = getRequestParamMap(appId, callbackCommand);
        try {
            this.httpRequestUtils.doPost(this.httpClientProperties.getCallbackUrl(), Object.class, params, jsonBody, null);
        } catch (URISyntaxException e) {
            log.error(String.format("After callback: appid-[%d] command-[%s], exception：%s", appId, callbackCommand, e.getMessage()));
        }
    }

    private Map<String, Object> getRequestParamMap(Integer appId, String callbackCommand) {
        Map<String, Object> params = new HashMap<>();
        params.put("appId", appId);
        params.put("command", callbackCommand);
        return params;
    }


}
