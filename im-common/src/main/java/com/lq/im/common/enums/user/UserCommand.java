package com.lq.im.common.enums.user;

import com.lq.im.common.enums.command.Command;

public enum UserCommand implements Command {
    /**
     * 用户信息被修改 0D4000
     */
    USER_INFO_MODIFIED(0x0FA0),
    ;

    private final int command;

    UserCommand(int command) {
        this.command = command;
    }

    @Override
    public int getCommand() {
        return this.command;
    }
}
