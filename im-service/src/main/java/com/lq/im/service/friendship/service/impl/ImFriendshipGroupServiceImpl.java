package com.lq.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lq.im.common.ResponseVO;
import com.lq.im.common.enums.DelFlagEnum;
import com.lq.im.common.enums.FriendShipErrorCodeEnum;
import com.lq.im.common.enums.UserErrorCodeEnum;
import com.lq.im.service.friendship.mapper.ImFriendshipGroupMapper;
import com.lq.im.service.friendship.mapper.ImFriendshipGroupMemberMapper;
import com.lq.im.service.friendship.model.ImFriendshipGroupDAO;
import com.lq.im.service.friendship.model.ImFriendshipGroupMemberDAO;
import com.lq.im.service.friendship.model.ImFriendshipRequestDAO;
import com.lq.im.service.friendship.model.req.AddFriendshipGroupReq;
import com.lq.im.service.friendship.model.req.RemoveFriendshipGroupReq;
import com.lq.im.service.friendship.service.ImFriendshipGroupService;
import com.lq.im.service.user.model.ImUserDAO;
import com.lq.im.service.user.model.resp.AddGroupResp;
import com.lq.im.service.user.service.ImUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * @ClassName: ImFriendshipGroupServiceImpl
 * @Author: LiQi
 * @Date: 2023-05-31 15:58
 * @Version: V1.0
 * @Description:
 */
@Service
public class ImFriendshipGroupServiceImpl implements ImFriendshipGroupService {

    @Resource
    private ImFriendshipGroupMapper imFriendshipGroupMapper;

    @Resource
    private ImFriendshipGroupMemberMapper imFriendshipGroupMemberMapper;

    @Resource
    private ImUserService imUserService;


    @Override
    public ResponseVO addGroup(AddFriendshipGroupReq req) {
        // 1. 首先判断用户是否存在
        ResponseVO<ImUserDAO> singleUserInfo = this.imUserService.getSingleUserInfo(req.getUserId(), req.getAppId());
        if(singleUserInfo == null || !singleUserInfo.isOk()) {
            // 不存在则返回用户不存在
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
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIEND_SHIP_GROUP_CREATE_ERROR);
            }
        } else {
            // 2.2 若存在则判断del_flag是否为1
            if(imFriendshipGroupDAO.getDelFlag() == DelFlagEnum.NORMAL.getCode()) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIEND_SHIP_GROUP_IS_EXIST);
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
        if(!CollectionUtils.isEmpty(req.getFriendIdList())) {
            req.getFriendIdList().forEach(friendId -> {
                try {
                    int insert = this.imFriendshipGroupMemberMapper.insert(new ImFriendshipGroupMemberDAO(groupDAO.getId(), friendId));
                    if(insert == 1) {
                        addGroupResp.getSuccessUserIdList().add(friendId);
                    } else {
                        addGroupResp.getFailUserIdList().add(friendId);
                    }
                } catch (Exception e) {
                    addGroupResp.getFailUserIdList().add(friendId);
                }
            });
        }

        return ResponseVO.successResponse(addGroupResp);
    }

    @Override
    public ResponseVO removeGroup(RemoveFriendshipGroupReq req) {


        return null;
    }

    @Override
    public ResponseVO getGroup(String appId, String userId, String groupName) {
        return null;
    }

    @Override
    public Long modifyGroup(String appId, String userId, String groupName) {
        return null;
    }
}
