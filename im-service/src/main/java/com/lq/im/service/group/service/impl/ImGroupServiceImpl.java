package com.lq.im.service.group.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lq.im.common.ResponseVO;
import com.lq.im.common.enums.GroupErrorCodeEnum;
import com.lq.im.common.enums.GroupMemberRoleEnum;
import com.lq.im.common.enums.GroupStatusEnum;
import com.lq.im.common.enums.GroupTypeEnum;
import com.lq.im.service.group.mapper.ImGroupMapper;
import com.lq.im.service.group.model.ImGroupDAO;
import com.lq.im.service.group.model.ImGroupMemberDAO;
import com.lq.im.service.group.model.req.*;
import com.lq.im.service.group.model.resp.GetGroupWithMemberListResp;
import com.lq.im.service.group.service.ImGroupMemberService;
import com.lq.im.service.group.service.ImGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;
import java.util.Objects;

import static com.lq.im.service.user.service.impl.ImUserServiceImpl.ERROR_MESSAGE;

@Service
@Slf4j
public class ImGroupServiceImpl implements ImGroupService {

    @Resource
    private ImGroupMemberService imGroupMemberService;

    @Resource
    private ImGroupMapper imGroupMapper;

    @Override
    public ResponseVO<?> importGroup(ImportGroupReq req) {
        QueryWrapper<ImGroupDAO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId())
                .eq("group_id", req.getGroupId());
        if (StringUtils.isBlank(req.getGroupId())) {
            req.setGroupId(IdUtil.simpleUUID());
        } else if (this.imGroupMapper.selectCount(queryWrapper) > 0) {
            return ResponseVO.errorResponse(GroupErrorCodeEnum.GROUP_ALREADY_EXISTS);
        }
        if (req.getGroupType() == GroupTypeEnum.PUBLIC.getCode() && StringUtils.isBlank(req.getOwnerId())) {
            return ResponseVO.errorResponse(GroupErrorCodeEnum.PUBLIC_GROUP_MUST_HAVE_OWNER);
        }
        if (req.getCreateTime() == null) {
            req.setCreateTime(System.currentTimeMillis());
        }
        if (req.getUpdateTime() == null) {
            req.setUpdateTime(System.currentTimeMillis());
        }
        ImGroupDAO groupDAO = new ImGroupDAO();
        BeanUtils.copyProperties(req, groupDAO);
        groupDAO.setStatus(GroupStatusEnum.NORMAL.getCode());
        try{
            int result = this.imGroupMapper.insert(groupDAO);
            if (result != 1) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.IMPORT_GROUP_ERROR);
            }
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e);
            return ResponseVO.errorResponse(GroupErrorCodeEnum.IMPORT_GROUP_ERROR);
        }
        return ResponseVO.successResponse();
    }

    @Override
    @Transactional
    public ResponseVO<?> createGroup(CreateGroupReq req) {
        boolean isAdmin = false;
        if (!isAdmin) {
            req.setOwnerId(req.getOperator());
        }
        ImGroupDAO groupDAO;
        if (StringUtils.isBlank(req.getGroupId())) {
            groupDAO = new ImGroupDAO();
            req.setGroupId(IdUtil.simpleUUID());
        } else {
            ResponseVO<ImGroupDAO> responseVO = this.getGroup(req.getAppId(), req.getGroupId());
            if (responseVO.isOk() && responseVO.getData().getStatus() == GroupStatusEnum.NORMAL.getCode()) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.GROUP_ALREADY_EXISTS);
            } else if (responseVO.isOk()) {
                groupDAO = responseVO.getData();
            } else {
                groupDAO = new ImGroupDAO();
            }
        }
        if (req.getGroupType() == GroupTypeEnum.PUBLIC.getCode() && StringUtils.isBlank(req.getOwnerId())) {
            return ResponseVO.errorResponse(GroupErrorCodeEnum.PUBLIC_GROUP_MUST_HAVE_OWNER);
        }
        BeanUtils.copyProperties(req, groupDAO);
        groupDAO.setStatus(GroupStatusEnum.NORMAL.getCode());
        groupDAO.setCreateTime(System.currentTimeMillis());
        groupDAO.setUpdateTime(System.currentTimeMillis());
        try {
            int insertResult = this.imGroupMapper.insert(groupDAO);
            if (insertResult != 1) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.CREATE_GROUP_ERROR);
            }
            // 添加群主
            ImGroupMemberDTO groupOwnerDTO = new ImGroupMemberDTO(req.getOwnerId(), null,
                    GroupMemberRoleEnum.OWNER.getCode(), null, System.currentTimeMillis(), null);
            ResponseVO<?> responseVO = this.imGroupMemberService.addGroupMember(req.getAppId(), req.getGroupId(), groupOwnerDTO);
            if (!responseVO.isOk()) {
                return responseVO;
            }
            // 添加群成员
            for (ImGroupMemberDTO memberDTO : req.getMemberList()) {
                this.imGroupMemberService.addGroupMember(req.getAppId(), req.getGroupId(), memberDTO);
            }
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e);
            return ResponseVO.errorResponse(GroupErrorCodeEnum.CREATE_GROUP_ERROR);
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> updateGroupInfo(UpdateGroupInfoReq req) {
        ResponseVO<ImGroupDAO> responseVO = this.getGroup(req.getAppId(), req.getGroupId());
        if (!responseVO.isOk()) {
            return responseVO;
        }
        if (responseVO.getData().getStatus() == GroupStatusEnum.DISMISSED.getCode()) {
            return ResponseVO.errorResponse(GroupErrorCodeEnum.GROUP_IS_DISMISSED);
        }
        ImGroupDAO originalGroupInfo = responseVO.getData();
        boolean isAdmin = false;
        if (!isAdmin) {
            // 1. 检查操作人角色
            ResponseVO<ImGroupMemberDAO> memberInfoResponseVO = this.imGroupMemberService.getGroupMemberInfo(req.getAppId(), req.getGroupId(), req.getOperator());
            if (!memberInfoResponseVO.isOk()) {
                return memberInfoResponseVO;
            }
            ImGroupMemberDAO operatorMemberDAO = memberInfoResponseVO.getData();
            boolean isManager = operatorMemberDAO.getMemberRole() == GroupMemberRoleEnum.MANAGER.getCode();
            boolean isOwner = operatorMemberDAO.getMemberRole() == GroupMemberRoleEnum.OWNER.getCode();
            if (originalGroupInfo.getGroupType() == GroupTypeEnum.PUBLIC.getCode() && !isManager && !isOwner) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.THIS_OPERATION_NEEDS_MANAGER_ROLE);
            }
        }
        ImGroupDAO groupDAO = new ImGroupDAO();
        BeanUtils.copyProperties(req, groupDAO);
        groupDAO.setStatus(GroupStatusEnum.NORMAL.getCode());
        groupDAO.setUpdateTime(System.currentTimeMillis());
        try {
            QueryWrapper<ImGroupDAO> wrapper = new QueryWrapper<>();
            wrapper.eq("app_id", groupDAO.getAppId())
                    .eq("group_id", groupDAO.getGroupId());
            int updateResult = this.imGroupMapper.update(groupDAO, wrapper);
            if (updateResult != 1) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.UPDATE_GROUP_BASE_INFO_ERROR);
            }
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e);
            return ResponseVO.errorResponse(GroupErrorCodeEnum.UPDATE_GROUP_BASE_INFO_ERROR);
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<ImGroupDAO> getGroup(Integer appId, String groupId) {
        QueryWrapper<ImGroupDAO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", appId)
                .eq("group_id", groupId);
        ImGroupDAO groupDAO = this.imGroupMapper.selectOne(queryWrapper);
        if (groupDAO == null) {
            return ResponseVO.errorResponse(GroupErrorCodeEnum.GROUP_DOES_NOT_EXIST);
        }
        return ResponseVO.successResponse(groupDAO);
    }

    @Override
    public ResponseVO<?> getGroupWithMemberList(Integer appId, String groupId) {
        ResponseVO<ImGroupDAO> responseVO = this.getGroup(appId, groupId);
        if (!responseVO.isOk()) {
            return responseVO;
        }
        ImGroupDAO groupInfo = responseVO.getData();
        GetGroupWithMemberListResp resp = new GetGroupWithMemberListResp();
        BeanUtils.copyProperties(groupInfo, resp);
        ResponseVO<List<ImGroupMemberDTO>> groupMemberListResponseVO = this.imGroupMemberService.getGroupMemberList(appId, groupId);
        if (!groupMemberListResponseVO.isOk()) {
            return groupMemberListResponseVO;
        }
        List<ImGroupMemberDTO> groupMemberDTOList = groupMemberListResponseVO.getData();
        resp.setMemberList(groupMemberDTOList);
        return ResponseVO.successResponse(resp);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseVO<?> getJoinedGroupList(GetJoinedGroupListReq req) {
        ResponseVO<?> response = this.imGroupMemberService.getGroupIdListBy(req.getAppId(), req.getUserId());
        if (!response.isOk()) {
            return response;
        }
        List<String> groupIdList = (List<String>) response.getData();
        QueryWrapper<ImGroupDAO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId())
                .in("group_type", req.getGroupTypeList())
                .in("group_id", groupIdList);
        List<ImGroupDAO> groupDAOList;
        try {
            groupDAOList = this.imGroupMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e);
            return ResponseVO.errorResponse(GroupErrorCodeEnum.GET_JOINED_GROUP_ERROR);
        }
        return ResponseVO.successResponse(groupDAOList);
    }

    @Override
    public ResponseVO<?> dismissGroup(DismissGroupReq req) {
        ResponseVO<ImGroupDAO> response = this.getGroup(req.getAppId(), req.getGroupId());
        if (!response.isOk()) {
            return response;
        }
        ImGroupDAO groupData = response.getData();
        boolean isAdmin = false;
        if (!isAdmin) {
            if (groupData.getGroupType() == GroupTypeEnum.PRIVATE.getCode()) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.THIS_OPERATION_NEEDS_APP_MANAGER_ROLE);
            }
            boolean isOwner = Objects.equals(req.getOperator(), groupData.getOwnerId());
            if (groupData.getGroupType() == GroupTypeEnum.PUBLIC.getCode() && !isOwner) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.THIS_OPERATION_NEEDS_OWNER_ROLE);
            }
        }
        UpdateWrapper<ImGroupDAO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", GroupStatusEnum.DISMISSED.getCode())
                .set("update_time", System.currentTimeMillis())
                .eq("app_id", req.getAppId())
                .eq("group_id", req.getGroupId());
        try {
            int updateResult = this.imGroupMapper.update(null, updateWrapper);
            if (updateResult != 1) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.DISMISS_GROUP_ERROR);
            }
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e);
            return ResponseVO.errorResponse(GroupErrorCodeEnum.DISMISS_GROUP_ERROR);
        }
        return ResponseVO.successResponse();
    }
}
