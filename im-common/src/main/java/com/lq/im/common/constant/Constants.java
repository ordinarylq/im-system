package com.lq.im.common.constant;

public class Constants {

    public static final String USER_ID = "userId";

    public static final String APP_ID = "appId";

    public static final String CLIENT_TYPE = "clientType";

    public static class RedisConstants {
        /**
         * Redis Hash key format: {appId}:userSession:{userId}
         */
        public static final String USER_SESSION = ":userSession:";
    }
}
