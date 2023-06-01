package com.lq.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lq.im.common.ResponseVO;
import com.lq.im.common.enums.FriendShipErrorCodeEnum;
import com.lq.im.common.enums.UserErrorCodeEnum;
import com.lq.im.service.friendship.mapper.ImFriendshipGroupMemberMapper;
import com.lq.im.service.friendship.model.ImFriendshipGroupDAO;
import com.lq.im.service.friendship.model.ImFriendshipGroupMemberDAO;
import com.lq.im.service.friendship.model.req.AddFriendshipGroupMemberReq;
import com.lq.im.service.friendship.model.req.RemoveFriendshipGroupMemberReq;
import com.lq.im.service.friendship.model.resp.AddFriendshipGroupMemberResp;
import com.lq.im.service.friendship.model.resp.RemoveFriendshipGroupMemberResp;
import com.lq.im.service.friendship.service.ImFriendshipGroupMemberService;
import com.lq.im.service.friendship.service.ImFriendshipGroupService;
import com.lq.im.service.user.model.ImUserDAO;
import com.lq.im.service.user.service.ImUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName: ImFriendshipGroupMemberServiceImpl
 * @Author: LiQi
 * @Date: 2023-05-31 16:48
 * @Version: V1.0
 * @Description:
 */
@Service
public class ImFriendshipGroupMemberServiceImpl implements ImFriendshipGroupMemberService {

    @Resource
    private ImFriendshipGroupMemberMapper imFriendshipGroupMemberMapper;

    @Resource
    private ImFriendshipGroupService imFriendshipGroupService;

    @Resource
    private ImUserService imUserService;

    @Override
    public Integer addGroupMember(Long groupId, String userId) {
        try {
            return this.imFriendshipGroupMemberMapper.insert(
                    new ImFriendshipGroupMemberDAO(groupId, userId));
        } catch (Exception e) {
            e.printStackTrace();
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
    public ResponseVO addMultipleMembers(AddFriendshipGroupMemberReq req) {
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
                            (friendUserId, FriendShipErrorCodeEnum.FRIEND_SHIP_GROUP_MEMBER_EXIST.getError()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                resp.getFailUserItemList().add(new AddFriendshipGroupMemberResp.ResultItem(friendUserId, e.getMessage()));
            }
        }
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO removeMultipleMembers(RemoveFriendshipGroupMemberReq req) {
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
                            FriendShipErrorCodeEnum.FRIEND_SHIP_GROUP_MEMBER_NOT_EXIST.getError()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                resp.getFailUserItemList().add(new RemoveFriendshipGroupMemberResp.ResultItem(friendUserId, e.getMessage()));
            }
        }
        return ResponseVO.successResponse(resp);
    }
}
