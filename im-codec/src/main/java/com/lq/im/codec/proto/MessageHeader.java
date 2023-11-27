package com.lq.im.codec.proto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 消息头
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageHeader {
    /**
     * 指令
     */
    private Integer command;

    /**
     * 协议版本
     */
    private Integer version;

    /**
     * 客户端类型
     */
    private Integer clientType;

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 消息体类型
     * 0x0 JSON 默认值
     * 0x1 Protobuf
     * 0x2 XML
     */
    private Integer messageBodyType = 0x0;

    /**
     * IMEI长度
     */
    private Integer imeiLength;

    /**
     * 消息体长度
     */
    private Integer messageBodyLength;

    /**
     * IMEI号
     */
    private String imei;
}
