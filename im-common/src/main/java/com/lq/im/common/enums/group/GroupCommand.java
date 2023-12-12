package com.lq.im.common.enums.group;

import com.lq.im.common.enums.command.Command;

public enum GroupCommand implements Command {

    CREATE_CHAT_GROUP(0x1388),
    UPDATE_CHAT_GROUP_INFO(0x1389),
    DISMISS_CHAT_GROUP(0x138A),
    HAND_OVER_CHAT_GROUP(0x138B),
    MUTE_CHAT_GROUP(0x138C),
    ADD_GROUP_MEMBER(0x138D),
    REMOVE_GROUP_MEMBER(0x138E),
    UPDATE_GROUP_MEMBER(0x138F)
    ;
    private final int command;

    GroupCommand(int command) {
        this.command = command;
    }

    @Override
    public int getCommand() {
        return this.command;
    }
}
