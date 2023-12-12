package com.lq.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lq.im.common.ResponseVO;
import com.lq.im.common.enums.friendship.FriendshipCommand;
import com.lq.im.common.enums.user.DelFlagEnum;
import com.lq.im.common.enums.friendship.FriendShipErrorCodeEnum;
import com.lq.im.common.enums.user.UserErrorCodeEnum;
import com.lq.im.common.model.UserClientDTO;
import com.lq.im.service.friendship.mapper.ImFriendshipGroupMapper;
import com.lq.im.service.friendship.model.ImFriendshipGroupDAO;
import com.lq.im.service.friendship.model.message.AddFriendshipGroupDTO;
import com.lq.im.service.friendship.model.req.AddFriendshipGroupReq;
import com.lq.im.service.friendship.model.req.RemoveFriendshipGroupReq;
import com.lq.im.service.friendship.model.resp.DeleteFriendshipGroupResp;
import com.lq.im.service.friendship.service.ImFriendshipGroupMemberService;
import com.lq.im.service.friendship.service.ImFriendshipGroupService;
import com.lq.im.service.user.model.ImUserDAO;
import com.lq.im.service.user.model.resp.AddGroupResp;
import com.lq.im.service.user.service.ImUserService;
import com.lq.im.service.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static com.lq.im.service.user.service.impl.ImUserServiceImpl.ERROR_MESSAGE;

@Service
@Slf4j
public class ImFriendshipGroupServiceImpl implements ImFriendshipGroupService {

    @Resource
    private ImFriendshipGroupMapper imFriendshipGroupMapper;
    @Resource
    private ImFriendshipGroupMemberService imFriendshipGroupMemberService;
    @Resource
    private ImUserService imUserService;
    @Resource
    private MessageUtils messageUtils;


    @Override
    public ResponseVO<?> addGroup(AddFriendshipGroupReq req) {
        // 1. 首先判断用户是否存在
        ResponseVO<ImUserDAO> singleUserInfo = this.imUserService.getSingleUserInfo(req.getUserId(), req.getAppId());
        if(singleUserInfo == null || !singleUserInfo.isOk()) {
            return ResponseVO.errorResponse(UserErrorCodeEnum.USER_IS_NOT_EXIST);
        }
        // 2. 若存在则先查询指定分组名称的分组是否存在
        QueryWrapper<ImFriendshipGroupDAO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId())
                .eq("user_id", req.getUserId())
                .eq("group_name", req.getGroupName());
        ImFriendshipGroupDAO imFriendshipGroupDAO = this.imFriendshipGroupMapper.selectOne(queryWrapper);
        ImFriendshipGroupDAO groupDAO;
        if(imFriendshipGroupDAO == null) {
            // 2.1 若不存在则插入数据
            groupDAO = new ImFriendshipGroupDAO();
            groupDAO.setGroupName(req.getGroupName());
            groupDAO.setDelFlag(DelFlagEnum.NORMAL.getCode());
            groupDAO.setAppId(req.getAppId());
            groupDAO.setUserId(req.getUserId());
            groupDAO.setCreateTime(System.currentTimeMillis());
            groupDAO.setUpdateTime(System.currentTimeMillis());

            int insert = this.imFriendshipGroupMapper.insert(groupDAO);
            if(insert != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.CREATE_FRIEND_GROUP_ERROR);
            }
        } else {
            // 2.2 若存在则判断del_flag是否为1
            if(imFriendshipGroupDAO.getDelFlag() == DelFlagEnum.NORMAL.getCode()) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIEND_GROUP_ALREADY_EXISTS);
            }
            // 2.2.1 若已被删除则更新del_flag为0
            groupDAO = new ImFriendshipGroupDAO();
            groupDAO.setId(imFriendshipGroupDAO.getId());
            groupDAO.setDelFlag(DelFlagEnum.NORMAL.getCode());
            groupDAO.setUpdateTime(System.currentTimeMillis());

            this.imFriendshipGroupMapper.updateById(groupDAO);
        }

        // 3.插入成员表
        AddGroupResp addGroupResp = new AddGroupResp();
        req.getFriendIdList().forEach(friendId -> {
            try {
                int insert = this.imFriendshipGroupMemberService.addGroupMember(groupDAO.getId(), friendId);
                if(insert == 1) {
                    addGroupResp.getSuccessUserIdList().add(friendId);
                } else {
                    addGroupResp.getFailUserIdList().add(friendId);
                }
            } catch (Exception e) {
                log.error(ERROR_MESSAGE, e);
                addGroupResp.getFailUserIdList().add(friendId);
            }
        });
        AddFriendshipGroupDTO addFriendshipGroupMsg = new AddFriendshipGroupDTO();
        BeanUtils.copyProperties(req, addFriendshipGroupMsg);
        UserClientDTO userClient = new UserClientDTO();
        BeanUtils.copyProperties(req, userClient);
        this.messageUtils.sendMessage(FriendshipCommand.ADD_FRIEND_GROUP, addFriendshipGroupMsg, userClient);
        return ResponseVO.successResponse(addGroupResp);
    }

    @Override
    @Transactional
    public ResponseVO<?> removeGroup(RemoveFriendshipGroupReq req) {
        // 1. 首先判断用户是否存在
        ResponseVO<ImUserDAO> singleUserInfo = this.imUserService.getSingleUserInfo(req.getUserId(), req.getAppId());
        if(singleUserInfo == null || !singleUserInfo.isOk()) {
            return ResponseVO.errorResponse(UserErrorCodeEnum.USER_IS_NOT_EXIST);
        }

        DeleteFriendshipGroupResp resp = new DeleteFriendshipGroupResp();
        for(String groupName: req.getGroupNameList()) {
            // 2. 先查询是否存在该分组
            QueryWrapper<ImFriendshipGroupDAO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("app_id", req.getAppId())
                    .eq("user_id", req.getUserId())
                    .eq("group_name", groupName)
                    .eq("del_flag", DelFlagEnum.NORMAL.getCode());

            ImFriendshipGroupDAO imFriendshipGroupDAO = this.imFriendshipGroupMapper.selectOne(queryWrapper);
            if(imFriendshipGroupDAO == null) {
                resp.getFailGroupItemList().add(new DeleteFriendshipGroupResp.ResultItem(
                        groupName, FriendShipErrorCodeEnum.FRIEND_GROUP_NOT_EXISTS.getError()));
                continue;
            }
            // 1.软删除分组 2.清空成员列表
            ImFriendshipGroupDAO updateGroupDAO = new ImFriendshipGroupDAO();
            updateGroupDAO.setId(imFriendshipGroupDAO.getId());
            updateGroupDAO.setUpdateTime(System.currentTimeMillis());
            updateGroupDAO.setDelFlag(DelFlagEnum.DELETED.getCode());
            int deleteResult = this.imFriendshipGroupMapper.updateById(updateGroupDAO);
            if(deleteResult != 1) {
                resp.getFailGroupItemList().add(new DeleteFriendshipGroupResp.ResultItem(
                        groupName, FriendShipErrorCodeEnum.DELETE_FRIEND_GROUP_ERROR.getError()));
            }
            resp.getSuccessGroupNameList().add(groupName);
            this.imFriendshipGroupMemberService.clearGroupMember(imFriendshipGroupDAO.getId());
        }
        UserClientDTO userClient = new UserClientDTO();
        BeanUtils.copyProperties(req, userClient);
        this.messageUtils.sendMessage(FriendshipCommand.REMOVE_FRIEND_GROUP, req, userClient);
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<ImFriendshipGroupDAO> getGroup(Integer appId, String userId, String groupName) {
        // 1. 首先判断用户是否存在
        ResponseVO<ImUserDAO> singleUserInfo = this.imUserService.getSingleUserInfo(userId, appId);
        if(singleUserInfo == null || !singleUserInfo.isOk()) {
            return ResponseVO.errorResponse(UserErrorCodeEnum.USER_IS_NOT_EXIST);
        }
        QueryWrapper<ImFriendshipGroupDAO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", appId)
                .eq("user_id", userId)
                .eq("group_name", groupName)
                .eq("del_flag", DelFlagEnum.NORMAL.getCode());
        ImFriendshipGroupDAO imFriendshipGroupDAO = this.imFriendshipGroupMapper.selectOne(queryWrapper);
        if(imFriendshipGroupDAO == null) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIEND_GROUP_NOT_EXISTS);
        }
        return ResponseVO.successResponse(imFriendshipGroupDAO);
    }

    @Override
    public Long modifyGroup(Integer appId, String userId, String groupName) {
        return null;
    }
}
