package com.lq.im.common.constant;

public class Constants {

    public static final String USER_ID = "userId";

    public static final String APP_ID = "appId";

    public static final String CLIENT_TYPE = "clientType";

    public static final String DEVICE_IMEI = "imei";

    public static final String LAST_READ_TIME = "lastReadTime";

    public static class RedisConstants {
        public static final String REDIS_KEY_SEPARATOR = ":";

        /**
         * Redis Hash key format: {appId}:userSession:{userId}
         */
        public static final String USER_SESSION = REDIS_KEY_SEPARATOR + "userSession" + REDIS_KEY_SEPARATOR;

        /**
         * 多端登录时的发布订阅频道名称
         */
        public static final String USER_LOGIN_CHANNEL = "user-login";

        public static final String USER_SIGNATURE = REDIS_KEY_SEPARATOR + "userSignature" + REDIS_KEY_SEPARATOR;

        public static final String MESSAGE_SEQUENCE = "messageSeq";
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

        public static final String STORE_P2P_MESSAGE = "storePeerToPeerMessage";
    }

    public static class ZkConstants {
        public static final String ROOT_NODE = "/im-coreRoot";

        public static final String TCP_NODE = "/tcp";

        public static final String WEB_NODE = "/web";
    }

    public static class CallbackCommand {
        public static final String AFTER_USER_INFO_MODIFIED = "user.modify.after";
        public static final String BEFORE_ADD_FRIENDSHIP = "friendship.add.before";
        public static final String AFTER_ADD_FRIENDSHIP = "friendship.add.after";
        public static final String AFTER_MODIFY_FRIENDSHIP = "friendship.modify.after";
        public static final String AFTER_DELETE_FRIENDSHIP = "friendship.delete.after";
        public static final String AFTER_BLOCK_FRIEND = "friendship.block.after";
        public static final String AFTER_UNBLOCK_FRIEND = "friendship.unblock.after";
        public static final String AFTER_CREATE_CHAT_GROUP = "group.create.after";
        public static final String AFTER_MODIFY_CHAT_GROUP = "group.modify.after";
        public static final String AFTER_DISMISS_CHAT_GROUP = "group.dismiss.after";
        public static final String AFTER_DELETE_CHAT_GROUP_MEMBER = "group.member.delete.after";
        public static final String BEFORE_ADD_CHAT_GROUP_MEMBER = "group.member.add.before";
        public static final String AFTER_ADD_CHAT_GROUP_MEMBER = "group.member.add.after";

    }

}
