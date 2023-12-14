package com.lq.im.service.config;

import com.alibaba.fastjson.JSONObject;
import com.lq.im.common.enums.gateway.GateWayError;
import com.lq.im.common.exception.ApplicationException;
import com.lq.im.common.utils.Base64Utils;
import com.lq.im.common.utils.HmacUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.lq.im.common.utils.UserSignatureConstants.*;

/**
 * (appid-operatorid-createtime-timeduration-hashvalue)usersig => Base64
 */
@Slf4j
@Data
public class UserSignature {

    private int appId;
    private String operatorId;
    private long timeoutDuration;
    private long createTime;
    private String hashValue;

    public UserSignature(int appId, String operatorId, long timeoutDuration, long createTime, String hashValue) {
        this.appId = appId;
        this.operatorId = operatorId;
        this.timeoutDuration = timeoutDuration;
        this.createTime = createTime;
        this.hashValue = hashValue;
    }

    public static UserSignature parseSignature(String signature) {
        JSONObject jsonObject = UserSignature.decodeToObject(signature);
        int appId = jsonObject.getInteger(APPID);
        String operatorId = jsonObject.getString(OPERATOR);
        long timeoutDuration  = jsonObject.getLong(TIMEOUT_DURATION);
        long createTime = jsonObject.getLong(CREATE_TIME);
        String hashValue = jsonObject.getString(HASH_VALUE);
        return new UserSignature(appId, operatorId, timeoutDuration, createTime, hashValue);
    }

    public static JSONObject decodeToObject(String encodedUserSignature) {
        if (StringUtils.isNotEmpty(encodedUserSignature)) {
            byte[] userSignature = Base64Utils.decode(encodedUserSignature);
            return JSONObject.parseObject(new String(userSignature, StandardCharsets.UTF_8));
        }
        return new JSONObject(true);
    }

    public void check(int expectedAppId, String expectedOperatorId) {
        checkAppId(expectedAppId);
        checkOperatorId(expectedOperatorId);
        checkIfExpired();
    }

    public void checkAppId(int expectedAppId) {
        if (!Objects.equals(expectedAppId, appId)) {
            throw new ApplicationException(GateWayError.USER_SIGNATURE_ERROR);
        }
    }

    public void checkOperatorId(String expectedOperatorId) {
        if (!Objects.equals(expectedOperatorId, operatorId)) {
            throw new ApplicationException(GateWayError.OPERATOR_DOES_NOT_MATCH_SIGNATURE);
        }
    }

    public void checkIfExpired() {
        long expectedExpireTime = createTime + timeoutDuration;
        if (timeoutDuration == 0 || expectedExpireTime < System.currentTimeMillis()) {
            throw new ApplicationException((GateWayError.USER_SIGNATURE_EXPIRED));
        }
    }

    public boolean checkHashValue(String privateKey) {
        String hashInput = getHashInput();
        String actualHashValue = HmacUtils.hash(privateKey, hashInput);
        return actualHashValue.equals(hashValue);
    }

    public String generateUserSignature(String privateKey) {
        String userSignatureStr = getHashInput();
        hashValue = HmacUtils.hash(privateKey, userSignatureStr);
        return Base64Utils.encodeWithoutPadding(getUserSignature().getBytes(StandardCharsets.UTF_8));
    }

    public String getHashInput() {
        return OPERATOR + ":" + operatorId + "\n" +
                APPID + ":" + appId + "\n" +
                CREATE_TIME + ":" + createTime + "\n" +
                TIMEOUT_DURATION + ":" + timeoutDuration + "\n";
    }

    private String getUserSignature() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(APPID, appId);
        jsonObject.put(OPERATOR, operatorId);
        jsonObject.put(TIMEOUT_DURATION, timeoutDuration);
        jsonObject.put(CREATE_TIME, createTime);
        jsonObject.put(HASH_VALUE, hashValue);
        return jsonObject.toString();
    }

    public long getTimeout() {
        return createTime + timeoutDuration - System.currentTimeMillis();
    }

}
