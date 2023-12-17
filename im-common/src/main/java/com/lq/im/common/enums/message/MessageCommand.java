package com.lq.im.common.enums.message;

import com.lq.im.common.enums.command.Command;

public enum MessageCommand implements Command {

    /**
     * 单聊 0D1000
     */
    PEER_TO_PEER(0x03E8),

    /**
     * 消息确认 0D1001
     */
    MESSAGE_ACK(0x03E9)

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
