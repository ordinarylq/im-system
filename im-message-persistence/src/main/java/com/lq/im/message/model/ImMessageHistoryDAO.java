package com.lq.im.message.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_message_history")
public class ImMessageHistoryDAO {

    private Integer appId;

    private Long messageKey;

    @TableField("from_id")
    private String userId;

    @TableField("to_id")
    private String friendUserId;

    private String ownerId;

    private Long sequence;

    private String messageRandom;

    private Long messageTime;

    private Long createTime;
}
