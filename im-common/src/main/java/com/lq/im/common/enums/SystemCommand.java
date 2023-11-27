package com.lq.im.common.enums;

import com.lq.im.common.enums.command.Command;

public enum SystemCommand implements Command {
    /**
     * 登录
     * 0D9000
     */
    LOGIN(0x2328),
    /**
     * 退出登录
     * 0D9003
     */
    LOGOUT(0x232B),

    /**
     * 心跳
     * 0D9999
     */
    PING(0x270F)
    ;

    private final int command;

    SystemCommand(int command) {
        this.command = command;
    }

    @Override
    public int getCommand() {
        return this.command;
    }
}
