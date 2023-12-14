package com.lq.im.service.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.lq.im.common.ResponseVO;
import com.lq.im.common.enums.gateway.GateWayError;
import com.lq.im.common.utils.UserSignatureConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class GateWayInterceptor implements HandlerInterceptor {

    @Resource
    private UserSignatureChecker userSignatureChecker;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        int appId = 0;
        try {
            appId = Integer.parseInt(request.getParameter(UserSignatureConstants.APPID));
        } catch (NumberFormatException e) {
            writeErrorResponse(response, ResponseVO.errorResponse(GateWayError.APPID_NOT_EXIST));
        }
        String operatorId = request.getParameter(UserSignatureConstants.OPERATOR);
        if (StringUtils.isEmpty(operatorId)) {
            writeErrorResponse(response, ResponseVO.errorResponse(GateWayError.OPERATOR_ID_NOT_EXIST));
        }
        String userSignature = request.getParameter(UserSignatureConstants.SIGNATURE);
        if (StringUtils.isEmpty(userSignature)) {
            writeErrorResponse(response, ResponseVO.errorResponse(GateWayError.USER_SIGNATURE_NOT_EXIST));
        }
        ResponseVO<?> responseVO = this.userSignatureChecker.checkUserSignature(appId, operatorId, userSignature);
        if (!responseVO.isOk()) {
            writeErrorResponse(response, responseVO);
            return false;
        }
        return true;
    }

    private void writeErrorResponse(HttpServletResponse response, ResponseVO<?> responseVO) {
        configResponse(response);
        try (PrintWriter writer = response.getWriter()) {
            String responseBody = JSONObject.toJSONString(responseVO);
            writer.write(responseBody);
        } catch (IOException e) {
            log.error("Response writing error: {}", e.getMessage());
        }
    }

    private void configResponse(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
    }
}
