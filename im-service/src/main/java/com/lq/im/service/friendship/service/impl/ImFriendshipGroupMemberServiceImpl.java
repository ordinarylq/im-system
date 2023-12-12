package com.lq.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lq.im.common.ResponseVO;
import com.lq.im.common.enums.friendship.FriendShipErrorCodeEnum;
import com.lq.im.common.enums.friendship.FriendshipCommand;
import com.lq.im.common.enums.user.UserErrorCodeEnum;
import com.lq.im.common.model.UserClientDTO;
import com.lq.im.service.friendship.mapper.ImFriendshipGroupMemberMapper;
import com.lq.im.service.friendship.model.ImFriendshipGroupDAO;
import com.lq.im.service.friendship.model.ImFriendshipGroupMemberDAO;
import com.lq.im.service.friendship.model.message.AddGroupMemberDTO;
import com.lq.im.service.friendship.model.message.RemoveGroupMemberDTO;
import com.lq.im.service.friendship.model.req.AddFriendshipGroupMemberReq;
import com.lq.im.service.friendship.model.req.RemoveFriendshipGroupMemberReq;
import com.lq.im.service.friendship.model.resp.AddFriendshipGroupMemberResp;
import com.lq.im.service.friendship.model.resp.RemoveFriendshipGroupMemberResp;
import com.lq.im.service.friendship.service.ImFriendshipGroupMemberService;
import com.lq.im.service.friendship.service.ImFriendshipGroupService;
import com.lq.im.service.user.model.ImUserDAO;
import com.lq.im.service.user.service.ImUserService;
import com.lq.im.service.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.lq.im.service.user.service.impl.ImUserServiceImpl.ERROR_MESSAGE;

@Service
@Slf4j
public class ImFriendshipGroupMemberServiceImpl implements ImFriendshipGroupMemberService {

    @Resource
    private ImFriendshipGroupMemberMapper imFriendshipGroupMemberMapper;
    @Resource
    private ImFriendshipGroupService imFriendshipGroupService;
    @Resource
    private ImUserService imUserService;
    @Resource
    private MessageUtils messageUtils;

    @Override
    public Integer addGroupMember(Long groupId, String userId) {
        try {
            return this.imFriendshipGroupMemberMapper.insert(
                    new ImFriendshipGroupMemberDAO(groupId, userId));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e);
            return 0;
        }
    }

    @Override
    public Integer clearGroupMember(Long groupId) {
        try {
            QueryWrapper<ImFriendshipGroupMemberDAO> memberDAOQueryWrapper = new QueryWrapper<>();
            memberDAOQueryWrapper.eq("group_id", groupId);
            return this.imFriendshipGroupMemberMapper.delete(memberDAOQueryWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public ResponseVO<?> addMultipleMembers(AddFriendshipGroupMemberReq req) {
        // 1. 首先判断用户是否存在
        ResponseVO<ImUserDAO> singleUserInfo = this.imUserService.getSingleUserInfo(req.getUserId(), req.getAppId());
        if(singleUserInfo == null || !singleUserInfo.isOk()) {
            // 不存在则返回用户不存在
            return ResponseVO.errorResponse(UserErrorCodeEnum.USER_IS_NOT_EXIST);
        }

        // 2. 先判断分组是否存在
        ResponseVO<ImFriendshipGroupDAO> groupInfoResp = this.imFriendshipGroupService.getGroup(req.getAppId(), req.getUserId(), req.getGroupName());
        if(!groupInfoResp.isOk()) {
            return groupInfoResp;
        }
        // 3. 遍历待添加的好友列表
        AddFriendshipGroupMemberResp resp = new AddFriendshipGroupMemberResp();
        for (String friendUserId : req.getFriendIdList()) {
            // 2.1 若好友不存在则添加到失败列表中
            ResponseVO<ImUserDAO> friendUserInfo = this.imUserService.getSingleUserInfo(friendUserId, req.getAppId());
            if(friendUserInfo == null || !friendUserInfo.isOk()) {
                resp.getFailUserItemList().add(new AddFriendshipGroupMemberResp.ResultItem(
                        friendUserId, UserErrorCodeEnum.USER_IS_NOT_EXIST.getError()));
                continue;
            }
            // 2.2 好友存在则执行插入
            ImFriendshipGroupMemberDAO groupMemberDAO = new ImFriendshipGroupMemberDAO(groupInfoResp.getData().getId(), friendUserId);

            try {
                int insert = this.imFriendshipGroupMemberMapper.insert(groupMemberDAO);
                if(insert == 1) {
                    resp.getSuccessUserIdList().add(friendUserId);
                } else {
                    resp.getFailUserItemList().add(new AddFriendshipGroupMemberResp.ResultItem
                            (friendUserId, FriendShipErrorCodeEnum.FRIEND_GROUP_ALREADY_EXISTS.getError()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                resp.getFailUserItemList().add(new AddFriendshipGroupMemberResp.ResultItem(friendUserId, e.getMessage()));
            }
        }
        AddGroupMemberDTO addGroupMemberMsg = new AddGroupMemberDTO();
        addGroupMemberMsg.setAppId(req.getAppId());
        addGroupMemberMsg.setUserId(req.getUserId());
        addGroupMemberMsg.setGroupName(req.getGroupName());
        addGroupMemberMsg.setFriendIdList(resp.getSuccessUserIdList());
        UserClientDTO userClient = new UserClientDTO();
        BeanUtils.copyProperties(req, userClient);
        this.messageUtils.sendMessage(FriendshipCommand.ADD_FRIEND_GROUP_MEMBER, addGroupMemberMsg, userClient);
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<?> removeMultipleMembers(RemoveFriendshipGroupMemberReq req) {
        // 1. 首先判断用户是否存在
        ResponseVO<ImUserDAO> singleUserInfo = this.imUserService.getSingleUserInfo(req.getUserId(), req.getAppId());
        if(singleUserInfo == null || !singleUserInfo.isOk()) {
            // 不存在则返回用户不存在
            return ResponseVO.errorResponse(UserErrorCodeEnum.USER_IS_NOT_EXIST);
        }

        // 2. 先判断分组是否存在
        ResponseVO<ImFriendshipGroupDAO> groupInfoResp = this.imFriendshipGroupService.getGroup(req.getAppId(), req.getUserId(), req.getGroupName());
        if(!groupInfoResp.isOk()) {
            return groupInfoResp;
        }

        // 3. 遍历待删除的好友列表
        RemoveFriendshipGroupMemberResp resp = new RemoveFriendshipGroupMemberResp();
        for (String friendUserId : req.getFriendIdList()) {
            // 2.1 若好友不存在则添加到失败列表中
            ResponseVO<ImUserDAO> friendUserInfo = this.imUserService.getSingleUserInfo(friendUserId, req.getAppId());
            if(friendUserInfo == null || !friendUserInfo.isOk()) {
                resp.getFailUserItemList().add(new RemoveFriendshipGroupMemberResp.ResultItem(
                        friendUserId, UserErrorCodeEnum.USER_IS_NOT_EXIST.getError()));
                continue;
            }
            // 2.2 好友存在则执行删除
            QueryWrapper<ImFriendshipGroupMemberDAO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("group_id", groupInfoResp.getData().getId())
                    .eq("user_id", friendUserId);
            try {
                int delete = this.imFriendshipGroupMemberMapper.delete(queryWrapper);
                if(delete == 1) {
                    resp.getSuccessUserIdList().add(friendUserId);
                } else {
                    resp.getFailUserItemList().add(new RemoveFriendshipGroupMemberResp.ResultItem(friendUserId,
                            FriendShipErrorCodeEnum.FRIEND_IS_NOT_IN_GROUP.getError()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                resp.getFailUserItemList().add(new RemoveFriendshipGroupMemberResp.ResultItem(friendUserId, e.getMessage()));
            }
        }
        RemoveGroupMemberDTO removeGroupMemberMsg = new RemoveGroupMemberDTO();
        removeGroupMemberMsg.setAppId(req.getAppId());
        removeGroupMemberMsg.setUserId(req.getUserId());
        removeGroupMemberMsg.setGroupName(req.getGroupName());
        removeGroupMemberMsg.setFriendIdList(resp.getSuccessUserIdList());
        UserClientDTO userClient = new UserClientDTO();
        BeanUtils.copyProperties(req, userClient);
        this.messageUtils.sendMessage(FriendshipCommand.REMOVE_FRIEND_GROUP_MEMBER, removeGroupMemberMsg, userClient);
        return ResponseVO.successResponse(resp);
    }
}
