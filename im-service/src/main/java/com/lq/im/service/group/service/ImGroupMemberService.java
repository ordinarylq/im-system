package com.lq.im.service.group.service;

import com.lq.im.common.ResponseVO;
import com.lq.im.common.model.UserClientDTO;
import com.lq.im.service.group.model.ImGroupMemberDAO;
import com.lq.im.service.group.model.req.*;

import java.util.List;

public interface ImGroupMemberService {
    ResponseVO<?> importGroupMember(ImportGroupMemberReq req);

    ResponseVO<?> addGroupMember(Integer appId, String groupId, ImGroupMemberDTO groupMemberDTO);

    /**
     * 邀请他人加入群聊
     * 逻辑：
     * 1. 后台管理员，有权限邀请；
     * 2. 私有群，所有人都有权限邀请；
     * 3. 公有群，需要管理员审核
     *
     */
    ResponseVO<?> inviteUserIntoGroup(InviteUserReq req);

    ResponseVO<?> exitGroup(ExitGroupReq req);

    ResponseVO<?> removeMemberFromGroup(RemoveMemberReq req);

    /**
     * 内部调用
     */
    ResponseVO<?> leaveGroup(Integer appId, String groupId, String memberId);

    ResponseVO<ImGroupMemberDAO> getGroupMemberInfo(Integer appId, String groupId, String memberId);

    ResponseVO<List<ImGroupMemberDTO>> getGroupMemberList(Integer appId, String groupId);

    ResponseVO<?> getGroupIdListBy(Integer appId, String memberId);

    ResponseVO<List<ImGroupMemberDTO>> getGroupManagerList(Integer appId, String groupId);

    ResponseVO<?> updateGroupMemberInfo(UserClientDTO userClient, String groupId, ImGroupMemberDTO groupMemberDTO);

    ResponseVO<?> updateGroupMemberInfo(UpdateGroupMemberReq req);

    ResponseVO<?> muteGroupMember(MuteGroupMemberReq req);
}
