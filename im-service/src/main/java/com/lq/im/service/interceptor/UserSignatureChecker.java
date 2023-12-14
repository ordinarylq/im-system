package com.lq.im.service.interceptor;

import com.lq.im.common.ResponseVO;
import com.lq.im.common.constant.Constants;
import com.lq.im.common.enums.gateway.GateWayError;
import com.lq.im.common.exception.ApplicationException;
import com.lq.im.service.config.UserSignature;
import com.lq.im.service.config.ApplicationConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class UserSignatureChecker {

    @Resource
    private ApplicationConfigProperties applicationConfigProperties;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public ResponseVO<?> checkUserSignature(Integer appId, String operatorId, String signature) {
        String redisKey = appId + Constants.RedisConstants.USER_SIGNATURE + operatorId +
                Constants.RedisConstants.REDIS_KEY_SEPARATOR + signature;
        String result = this.stringRedisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isNotEmpty(result)) {
            return ResponseVO.successResponse();
        }
        UserSignature userSignature;
        try {
            userSignature = UserSignature.parseSignature(signature);
        } catch (Exception e) {
            return ResponseVO.errorResponse(GateWayError.USER_SIGNATURE_ERROR);
        }
        try {
            userSignature.check(appId, operatorId);
        } catch (ApplicationException e) {
            return ResponseVO.errorResponse(e.getCode(), e.getError());
        }
        String privateKey = applicationConfigProperties.getPrivateKey();
        boolean hashCheckResult = userSignature.checkHashValue(privateKey);
        if (hashCheckResult) {
            this.stringRedisTemplate.opsForValue().set(
                    redisKey, signature, userSignature.getTimeout(), TimeUnit.MILLISECONDS
            );
            return ResponseVO.successResponse();
        }
        return ResponseVO.errorResponse(GateWayError.USER_SIGNATURE_ERROR);

    }

}
