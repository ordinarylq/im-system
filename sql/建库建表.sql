create table im-core;

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
    `user_type`          tinyint       DEFAULT NULL COMMENT '用户类型 1-普通用户 2-客服 3-机器人',
    `del_flag`           tinyint       DEFAULT NULL COMMENT '删除标志 0-未删除 1-已删除',
    `extra`              varchar(1000) DEFAULT NULL COMMENT '拓展',
    PRIMARY KEY (`app_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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