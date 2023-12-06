package com.lq.im.service.callback.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "httpclient")
public class HttpClientProperties {
    /**
     * 最大连接数
     */
    private Integer maxTotal;
    /**
     * 最大并发连接数
     */
    private Integer defaultMaxPerRoute;
    /**
     * 连接超时时间(ms)
     */
    private Integer connectTimeout;
    /**
     * 数据传输最长时间(ms)
     */
    private Integer socketTimeout;
    /**
     * 是否检查连接可用性
     */
    private boolean checkStaleConnection;
    /**
     * 回调URL
     */
    private String callbackUrl;

    /**
     * 回调配置
     */
    private boolean afterUserInfoModified;

    private boolean beforeAddFriendship;

    private boolean afterAddFriendship;

    private boolean afterModifyFriendship;

    private boolean afterDeleteFriendship;

    private boolean afterBlockFriend;

    private boolean afterUnblockFriend;

    private boolean afterCreateChatGroup;

    private boolean afterModifyChatGroup;

    private boolean afterDismissChatGroup;

    private boolean afterDeleteChatGroupMember;

    private boolean beforeAddChatGroupMember;

    private boolean afterAddChatGroupMember;

}
