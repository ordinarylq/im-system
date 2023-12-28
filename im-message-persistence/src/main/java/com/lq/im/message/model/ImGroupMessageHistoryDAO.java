package com.lq.im.message.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_group_message_history")
public class ImGroupMessageHistoryDAO {

    private Integer appId;

    private String groupId;

    private Long messageKey;

    @TableField("from_id")
    private String userId;

    private Long sequence;

    private String messageRandom;

    private Long messageTime;

    private Long createTime;

}
