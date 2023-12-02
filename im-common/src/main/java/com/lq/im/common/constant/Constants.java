package com.lq.im.common.constant;

public class Constants {

    public static final String USER_ID = "userId";

    public static final String APP_ID = "appId";

    public static final String CLIENT_TYPE = "clientType";

    public static final String LAST_READ_TIME = "lastReadTime";

    public static class RedisConstants {
        /**
         * Redis Hash key format: {appId}:userSession:{userId}
         */
        public static final String USER_SESSION = ":userSession:";
    }

    public static class MessageQueueConstants {

        public static final String IM_TO_USER_SERVICE = "pipelineToUserService";

        public static final String IM_TO_MESSAGE_SERVICE = "pipelineToMessageService";

        public static final String IM_TO_GROUP_SERVICE = "pipelineToGroupService";

        public static final String IM_TO_FRIENDSHIP_SERVICE = "pipelineToFriendshipService";

        public static final String USER_SERVICE_TO_IM = "userServiceToPipeline";

        public static final String MESSAGE_SERVICE_TO_IM = "messageServiceToPipeline";

        public static final String GROUP_SERVICE_TO_IM = "groupServiceToPipeline";

        public static final String FRIENDSHIP_SERVICE_TO_IM = "friendshipServiceToPipeline";
    }
}
