package com.lq.im.codec.proto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Message {
    private MessageHeader header;

    private Object body;
}
