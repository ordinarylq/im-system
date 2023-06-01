create table im-core;

-- `im-core`.im_friendship definition

CREATE TABLE `im_friendship`
(
    `app_id`          int         NOT NULL COMMENT '应用id',
    `from_id`         varchar(50) NOT NULL COMMENT '用户id',
    `to_id`           varchar(50) NOT NULL COMMENT '好友id',
    `remark`          varchar(100)  DEFAULT NULL COMMENT '备注',
    `status`          tinyint       DEFAULT NULL COMMENT '状态 1-正常 2-删除 0-未添加',
    `black`           tinyint       DEFAULT NULL COMMENT '是否拉黑好友 1-正常 2-已拉黑',
    `black_sequence`  varchar(255)  DEFAULT NULL,
    `create_time`     bigint        DEFAULT NULL,
    `friend_sequence` bigint        DEFAULT NULL COMMENT 'seq',
    `add_source`      varchar(20)   DEFAULT NULL COMMENT '来源',
    `extra`           varchar(1000) DEFAULT NULL,
    PRIMARY KEY (`app_id`, `from_id`, `to_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- `im-core`.im_friendship_group definition

CREATE TABLE `im_friendship_group`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '分组id',
    `app_id`      int          NOT NULL COMMENT '应用id',
    `user_id`     varchar(30)  NOT NULL COMMENT '用户id',
    `group_name`  varchar(255) NOT NULL COMMENT '组名',
    `sequence`    bigint                DEFAULT NULL COMMENT '序列',
    `create_time` bigint       NOT NULL COMMENT '创建时间',
    `update_time` bigint       NOT NULL COMMENT '更新时间',
    `del_flag`    int          NOT NULL DEFAULT '0' COMMENT '删除标识 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `im_friendship_group_UN` (`app_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='好友分组';


-- `im-core`.im_friendship_group_member definition

CREATE TABLE `im_friendship_group_member`
(
    `group_id` bigint      NOT NULL COMMENT '分组id',
    `user_id`  varchar(50) NOT NULL COMMENT '好友id',
    PRIMARY KEY (`group_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='好友分组成员';


-- `im-core`.im_friendship_request definition

CREATE TABLE `im_friendship_request`
(
    `id`             int         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `from_id`        varchar(50) NOT NULL COMMENT '用户id',
    `to_id`          varchar(50) NOT NULL COMMENT '申请人id',
    `app_id`         int         NOT NULL COMMENT '应用id',
    `read_status`    int          DEFAULT NULL COMMENT '是否已读 0-未读 1-已读',
    `add_wording`    varchar(255) DEFAULT NULL COMMENT '好友申请附加信息',
    `remark`         varchar(50)  DEFAULT NULL COMMENT '备注',
    `approve_status` tinyint      DEFAULT NULL COMMENT '用户审批结果 1-同意 2-拒绝',
    `create_time`    bigint       DEFAULT NULL COMMENT '创建时间',
    `update_time`    bigint       DEFAULT NULL COMMENT '更新时间',
    `sequence`       bigint       DEFAULT NULL COMMENT 'seq',
    `add_source`     varchar(100) DEFAULT NULL COMMENT '来源',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- `im-core`.im_user definition

CREATE TABLE `im_user`
(
    `user_id`            varchar(30) NOT NULL COMMENT '用户id',
    `app_id`             int         NOT NULL COMMENT '应用id',
    `nick_name`          varchar(50)   DEFAULT NULL COMMENT '昵称',
    `user_sex`           tinyint       DEFAULT NULL COMMENT '性别 1-男 2-女 0-未设置/其他',
    `birth_day`          varchar(30)   DEFAULT NULL COMMENT '生日',
    `location`           varchar(50)   DEFAULT NULL COMMENT '所在地',
    `self_signature`     varchar(255)  DEFAULT NULL COMMENT '个性签名',
    `friend_allow_type`  tinyint       DEFAULT NULL COMMENT '添加好友方式 1-无需验证 2-需要验证',
    `photo`              varchar(500)  DEFAULT NULL COMMENT '头像地址',
    `password`           varchar(255)  DEFAULT NULL COMMENT '密码',
    `disable_add_friend` tinyint       DEFAULT NULL COMMENT '管理员禁止用户添加好友 0-未禁用 1-禁用',
    `silent_flag`        tinyint       DEFAULT NULL COMMENT '禁言标志 0-未禁言 1-禁言',
    `forbidden_flag`     tinyint       DEFAULT NULL COMMENT '禁用标志 0-未禁用 1-禁用',
    `user_type`          tinyint       DEFAULT NULL COMMENT '用户类型 1-普通用户 2-客服 3-机器人 100-App管理员',
    `del_flag`           tinyint       DEFAULT NULL COMMENT '删除标志 0-未删除 1-已删除',
    `extra`              varchar(1000) DEFAULT NULL COMMENT '拓展',
    PRIMARY KEY (`app_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- `im-core`.im_group definition

CREATE TABLE `im_group`
(
    `app_id`           int         NOT NULL COMMENT '应用id',
    `group_id`         varchar(50) NOT NULL COMMENT '群组id',
    `owner_id`         varchar(50) NOT NULL COMMENT '群主用户id',
    `group_type`       tinyint unsigned NOT NULL COMMENT '1-私有群 2-公开群',
    `group_name`       varchar(50) NOT NULL COMMENT '群组名称',
    `has_mute`         tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否开启群禁言 0-未开启 1-已开启',
    `status`           tinyint unsigned NOT NULL DEFAULT '0' COMMENT '群组状态 0-正常 1-已解散',
    `apply_join_type`  tinyint unsigned NOT NULL DEFAULT '0' COMMENT '申请加群处理方式 0-需要验证 1-自由加入 3-禁止加入',
    `introduction`     varchar(50)   DEFAULT NULL COMMENT '群简介',
    `notification`     varchar(1000) DEFAULT NULL COMMENT '群公告',
    `photo_url`        varchar(500)  DEFAULT NULL COMMENT '群组头像地址',
    `max_member_count` int           DEFAULT NULL COMMENT '最大群成员数量',
    `sequence`         bigint        DEFAULT NULL COMMENT 'seq',
    `create_time`      bigint        DEFAULT NULL COMMENT '创建时间',
    `update_time`      bigint        DEFAULT NULL COMMENT '更新时间',
    `extra`            varchar(1000) DEFAULT NULL COMMENT '扩展',
    PRIMARY KEY (`app_id`, `group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='群组';


-- `im-core`.im_group_member definition

CREATE TABLE `im_group_member`
(
    `id`          bigint       NOT NULL COMMENT '成员id',
    `app_id`      int          NOT NULL COMMENT '应用id',
    `group_id`    varchar(50)  NOT NULL COMMENT '群组id',
    `member_id`   varchar(100) NOT NULL COMMENT '成员用户id',
    `speak_date`  bigint        DEFAULT NULL COMMENT '禁言到期日期',
    `member_role` tinyint unsigned NOT NULL COMMENT '群成员类型 0-普通成员 1-管理员 2-群主 3-已退出',
    `alias`       varchar(50)   DEFAULT NULL COMMENT '群昵称',
    `join_time`   bigint        DEFAULT NULL COMMENT '加入时间',
    `leave_time`  bigint        DEFAULT NULL COMMENT '离开时间',
    `join_type`   varchar(50)   DEFAULT NULL COMMENT '加入方式',
    `extra`       varchar(1000) DEFAULT NULL COMMENT '扩展',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='群组成员';