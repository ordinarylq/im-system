package com.lq.im.service.message.service;

import com.lq.im.common.ResponseVO;
import com.lq.im.common.enums.friendship.FriendShipErrorCodeEnum;
import com.lq.im.common.enums.friendship.FriendshipStatusEnum;
import com.lq.im.common.enums.message.MessageError;
import com.lq.im.common.enums.user.UserEnabledStatus;
import com.lq.im.common.enums.user.UserMutedStatus;
import com.lq.im.service.config.ApplicationConfigProperties;
import com.lq.im.service.friendship.model.ImFriendshipDAO;
import com.lq.im.service.friendship.model.req.GetFriendshipReq;
import com.lq.im.service.friendship.service.ImFriendshipService;
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
}
