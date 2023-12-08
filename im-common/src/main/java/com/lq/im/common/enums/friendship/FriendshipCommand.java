package com.lq.im.common.enums.friendship;

import com.lq.im.common.enums.command.Command;

public enum FriendshipCommand implements Command {
    ADD_FRIEND(0x0BB8),
    UPDATE_FRIEND(0x0BB9),
    DELETE_FRIEND(0x0BBA),
    BLOCK_FRIEND(0x0BBB),
    UNBLOCK_FRIEND(0x0BBC),
    ADD_FRIEND_REQUEST(0x0BBD),
    READ_FRIEND_REQUEST(0x0BBE),
    APPROVE_FRIEND_REQUEST(0x0BBF),
    ADD_FRIEND_GROUP(0x0BC0),
    REMOVE_FRIEND_GROUP(0x0BC1),
    ADD_FRIEND_GROUP_MEMBER(0x0BC2),
    REMOVE_FRIEND_GROUP_MEMBER(0x0BC3)
    ;
    private final int command;

    FriendshipCommand(int command) {
        this.command = command;
    }

    @Override
    public int getCommand() {
        return this.command;
    }
}
