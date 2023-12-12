package com.lq.im.service.utils;

import com.alibaba.fastjson.JSONObject;
import com.lq.im.common.enums.gateway.LoginDeviceType;
import com.lq.im.common.enums.group.GroupCommand;
import com.lq.im.common.model.UserClientDTO;
import com.lq.im.service.group.model.message.AddGroupMemberDTO;
import com.lq.im.service.group.model.message.RemoveGroupMemberDTO;
import com.lq.im.service.group.model.message.UpdateGroupMemberDTO;
import com.lq.im.service.group.model.req.ImGroupMemberDTO;
import com.lq.im.service.group.service.ImGroupMemberService;
import org.springframework.stereotype.Component;
import com.lq.im.common.enums.command.Command;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Component
public class GroupMessageUtils {

    @Resource
    private MessageUtils messageUtils;
    @Resource
    private ImGroupMemberService groupMemberService;

    public void sendMessage(UserClientDTO userClient, GroupCommand command, Object data) {
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(data);
        String groupId = jsonObject.getString("groupId");
        List<ImGroupMemberDTO> managerList = this.groupMemberService.getGroupManagerList(userClient.getAppId(), groupId).getData();
        List<ImGroupMemberDTO> memberUserIdList =
                this.groupMemberService.getGroupMemberList(userClient.getAppId(), groupId).getData();
        switch (command) {
            case ADD_GROUP_MEMBER:
                AddGroupMemberDTO addGroupMemberDTO = jsonObject.toJavaObject(AddGroupMemberDTO.class);
                for (ImGroupMemberDTO memberDTO : managerList) {
                    sendMessage(userClient, GroupCommand.ADD_GROUP_MEMBER, data, memberDTO.getMemberId());
                }
                for (String memberId : addGroupMemberDTO.getInviteeIdList()) {
                    sendMessage(userClient, GroupCommand.ADD_GROUP_MEMBER, data, memberId);
                }
                break;
            case REMOVE_GROUP_MEMBER:
                RemoveGroupMemberDTO removeGroupMemberDTO = jsonObject.toJavaObject(RemoveGroupMemberDTO.class);
                sendMessage(userClient, GroupCommand.REMOVE_GROUP_MEMBER, data, removeGroupMemberDTO.getMemberId());
                for (ImGroupMemberDTO memberDTO : managerList) {
                    sendMessage(userClient, GroupCommand.REMOVE_GROUP_MEMBER, data, memberDTO.getMemberId());
                }
                break;
            case UPDATE_GROUP_MEMBER:
                UpdateGroupMemberDTO updateGroupMemberDTO = jsonObject.toJavaObject(UpdateGroupMemberDTO.class);
                sendMessage(userClient, GroupCommand.UPDATE_GROUP_MEMBER, data, updateGroupMemberDTO.getMemberId());
                for (ImGroupMemberDTO memberDTO : managerList) {
                    sendMessage(userClient, GroupCommand.ADD_GROUP_MEMBER, data, memberDTO.getMemberId());
                }
                break;
            default:
                for (ImGroupMemberDTO memberDTO : memberUserIdList) {
                    sendMessage(userClient, command, data, memberDTO.getMemberId());
                }
        }
    }
    private void sendMessage(UserClientDTO userClient, Command command, Object data, String memberId) {
        if (!Objects.equals(userClient.getClientType(), LoginDeviceType.WEBAPI.getCode()) &&
                Objects.equals(memberId, userClient.getUserId())) {
            this.messageUtils.sendMessageExceptOneDevice(command, data, userClient, memberId);
        } else {
            this.messageUtils.sendMessageToAllDevicesOfOneUser(userClient.getAppId(), memberId,
                    command, data);
        }
    }
}
