package com.lq.im.service.message.service;

import com.lq.im.common.ResponseVO;
import com.lq.im.common.enums.friendship.FriendShipErrorCodeEnum;
import com.lq.im.common.enums.friendship.FriendshipStatusEnum;
import com.lq.im.common.enums.group.GroupErrorCodeEnum;
import com.lq.im.common.enums.group.GroupMemberRoleEnum;
import com.lq.im.common.enums.group.GroupMuteTypeEnum;
import com.lq.im.common.enums.message.MessageError;
import com.lq.im.common.enums.user.UserEnabledStatus;
import com.lq.im.common.enums.user.UserMutedStatus;
import com.lq.im.service.config.ApplicationConfigProperties;
import com.lq.im.service.friendship.model.ImFriendshipDAO;
import com.lq.im.service.friendship.model.req.GetFriendshipReq;
import com.lq.im.service.friendship.service.ImFriendshipService;
import com.lq.im.service.group.model.ImGroupDAO;
import com.lq.im.service.group.model.ImGroupMemberDAO;
import com.lq.im.service.group.service.ImGroupMemberService;
import com.lq.im.service.group.service.ImGroupService;
import com.lq.im.service.user.model.ImUserDAO;
import com.lq.im.service.user.service.ImUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MessageCheckService {

    @Resource
    private ImUserService imUserService;
    @Resource
    private ImFriendshipService imFriendshipService;
    @Resource
    private ImGroupService imGroupService;
    @Resource
    private ImGroupMemberService imGroupMemberService;
    @Resource
    private ApplicationConfigProperties applicationConfigProperties;

    public ResponseVO<?> checkUserAndFriendship(Integer appId, String userId, String friendUserId) {
        ResponseVO<?> responseVO = checkIfUserIsForbiddenOrMuted(appId, userId);
        if (!responseVO.isOk()) {
            return responseVO;
        }
        return checkFriendship(appId, userId, friendUserId);
    }

    public ResponseVO<?> checkIfUserIsForbiddenOrMuted(Integer appId, String userId) {
        ResponseVO<ImUserDAO> userInfoResponse = this.imUserService.getSingleUserInfo(userId, appId);
        if (!userInfoResponse.isOk()) {
            return userInfoResponse;
        }
        ImUserDAO userInfo = userInfoResponse.getData();
        if (userInfo.getForbiddenFlag() == UserEnabledStatus.DISABLED.getCode()) {
            return ResponseVO.errorResponse(MessageError.SENDER_IS_DISABLED);
        }
        if (userInfo.getSilentFlag() == UserMutedStatus.MUTED.getCode()) {
            return ResponseVO.errorResponse(MessageError.SENDER_IS_MUTED);
        }
        return ResponseVO.successResponse();
    }

    public ResponseVO<?> checkFriendship(Integer appId, String userId, String friendUserId) {
        GetFriendshipReq friendshipReq = new GetFriendshipReq();
        friendshipReq.setAppId(appId);
        friendshipReq.setUserId(userId);
        friendshipReq.setFriendUserId(friendUserId);
        ResponseVO<?> friendshipResp = this.imFriendshipService.getFriendship(friendshipReq);
        if (!friendshipResp.isOk()) {
            return friendshipResp;
        }
        ImFriendshipDAO friendshipData = (ImFriendshipDAO) friendshipResp.getData();
        friendshipReq.setUserId(friendUserId);
        friendshipReq.setFriendUserId(userId);
        ResponseVO<?> otherFriendshipResp = this.imFriendshipService.getFriendship(friendshipReq);
        if (!otherFriendshipResp.isOk()) {
            return otherFriendshipResp;
        }
        ImFriendshipDAO otherFriendshipData = (ImFriendshipDAO) otherFriendshipResp.getData();
        // 1. 是否校验好友关系
        if (this.applicationConfigProperties.isEnableSocialNetworkCheck()) {
            if (friendshipData.getStatus() != FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIEND_IS_DELETED);
            }
            if (otherFriendshipData.getStatus() != FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.YOU_ARE_DELETED);
            }
        }
        // 2. 是否校验黑名单
        if (this.applicationConfigProperties.isEnableBlockListCheck()) {
            if (friendshipData.getBlock() != FriendshipStatusEnum.BLOCK_STATUS_NORMAL.getCode()) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIEND_IS_BLOCKED);
            }
            if (otherFriendshipData.getBlock() != FriendshipStatusEnum.BLOCK_STATUS_NORMAL.getCode()) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.YOU_ARE_BLOCKED);
            }
        }
        return ResponseVO.successResponse();
    }

    public ResponseVO<?> canGroupMemberSendMessage(Integer appId, String userId, String groupId) {
        // 1. 校验用户是否被停用、禁言
        ResponseVO<?> userInfoResp = checkIfUserIsForbiddenOrMuted(appId, userId);
        if (!userInfoResp.isOk()) {
            return userInfoResp;
        }
        // 2. 校验群
        ResponseVO<ImGroupDAO> groupInfoResp = this.imGroupService.getGroup(appId, groupId);
        // 2.1 群是否正常
        if (!groupInfoResp.isOk()) {
            return groupInfoResp;
        }
        ImGroupDAO groupInfo = groupInfoResp.getData();
        // 2.2 当前用户是否在群内
        ResponseVO<ImGroupMemberDAO> memberInfoResp =
                this.imGroupMemberService.getGroupMemberInfo(appId, groupId, userId);
        if (!memberInfoResp.isOk()) {
            return memberInfoResp;
        }
        // 判断是否离开
        ImGroupMemberDAO memberInfo = memberInfoResp.getData();
        if (memberInfo.getMemberRole() == GroupMemberRoleEnum.LEAVE.getCode()) {
            return ResponseVO.errorResponse(GroupErrorCodeEnum.USER_DID_NOT_JOIN_GROUP);
        }
        // 2.3 群是否被禁言，若禁言则只有管理员、群主可以发言
        if (!canSpeakInGroup(groupInfo, memberInfo)) {
            return ResponseVO.errorResponse(GroupErrorCodeEnum.GROUP_IS_MUTED);
        }
        return ResponseVO.successResponse();
    }

    private boolean canSpeakInGroup(ImGroupDAO groupInfo, ImGroupMemberDAO memberInfo) {
        return isGroupMemberManagerOrOwner(memberInfo)
                || (!isGroupMuted(groupInfo) && hasGroupMemberMuteExpired(memberInfo));
    }

    private boolean isGroupMuted(ImGroupDAO groupInfo) {
        return groupInfo.getHasMute() == GroupMuteTypeEnum.MUTE.getCode();
    }

    private boolean isGroupMemberManagerOrOwner(ImGroupMemberDAO memberInfo) {
        return memberInfo.getMemberRole() == GroupMemberRoleEnum.OWNER.getCode()
                || memberInfo.getMemberRole() == GroupMemberRoleEnum.MANAGER.getCode();
    }

    private boolean hasGroupMemberMuteExpired(ImGroupMemberDAO memberInfo) {
        return memberInfo.getSpeakDate() == null || memberInfo.getSpeakDate() <= System.currentTimeMillis();
    }

}
