package com.lq.im.common.enums.message;

import com.lq.im.common.enums.command.Command;

public enum MessageCommand implements Command {

    /**
     * 单聊 0D1000
     */
    PEER_TO_PEER(0x03E8),
    /**
     * 群聊 0D1002
     */
    PEER_TO_GROUP(0x03EA),
    /**
     * 消息确认 0D1001
     */
    MESSAGE_ACK(0x03E9),

    /**
     * 消息收到确认 0D1003
     */
    MESSAGE_RECEIVE_ACK(0x03EB),

    ;
    private final int command;

    MessageCommand(int command) {
        this.command = command;
    }

    @Override
    public int getCommand() {
        return this.command;
    }
}
