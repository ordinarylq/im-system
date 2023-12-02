package com.lq.im.service.group.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lq.im.common.ResponseVO;
import com.lq.im.common.enums.group.GroupErrorCodeEnum;
import com.lq.im.common.enums.group.GroupMemberRoleEnum;
import com.lq.im.common.enums.group.GroupTypeEnum;
import com.lq.im.service.group.mapper.ImGroupMemberMapper;
import com.lq.im.service.group.model.ImGroupDAO;
import com.lq.im.service.group.model.ImGroupMemberDAO;
import com.lq.im.service.group.model.req.*;
import com.lq.im.service.group.model.resp.ImportGroupMemberResp;
import com.lq.im.service.group.model.resp.InviteUserResp;
import com.lq.im.service.group.service.ImGroupMemberService;
import com.lq.im.service.group.service.ImGroupService;
import com.lq.im.service.user.model.ImUserDAO;
import com.lq.im.service.user.service.ImUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;
import java.util.Objects;

import static com.lq.im.service.user.service.impl.ImUserServiceImpl.ERROR_MESSAGE;

@Service
@Slf4j
public class ImGroupMemberServiceImpl implements ImGroupMemberService {

    @Resource
    private ImGroupMemberMapper imGroupMemberMapper;

    @Resource
    private ImGroupService imGroupService;

    @Resource
    private ImGroupMemberService imGroupMemberService;

    @Resource
    private ImUserService imUserService;

    @Override
    @Transactional
    public ResponseVO<?> importGroupMember(ImportGroupMemberReq req) {
        ResponseVO<?> response = this.imGroupService.getGroup(req.getAppId(), req.getGroupId());
        if (!response.isOk()) {
            return response;
        }
        ImportGroupMemberResp resp = new ImportGroupMemberResp();
        for (ImGroupMemberDTO memberDTO : req.getMemberList()) {
            ResponseVO<?> responseVO;
            try {
                responseVO = this.imGroupMemberService.addGroupMember(req.getAppId(), req.getGroupId(), memberDTO);
            } catch (Exception e) {
                log.error(ERROR_MESSAGE, e);
                responseVO = ResponseVO.errorResponse();
            }
            if (responseVO.isOk()) {
                resp.getSuccessMemberIdList().add(memberDTO.getMemberId());
            } else {
                ImportGroupMemberResp.ResultItem resultItem =
                        new ImportGroupMemberResp.ResultItem(memberDTO.getMemberId(), responseVO.getMsg());
                resp.getFailMemberItemList().add(resultItem);
            }
        }
        return ResponseVO.successResponse(resp);
    }

    @Override
    @Transactional
    public ResponseVO<?> addGroupMember(Integer appId, String groupId, ImGroupMemberDTO groupMemberDTO) {
        ResponseVO<ImUserDAO> responseVO = this.imUserService.getSingleUserInfo(groupMemberDTO.getMemberId(), appId);
        if (!responseVO.isOk()) {
            return responseVO;
        }
        if (groupMemberDTO.getMemberRole() != null
                && groupMemberDTO.getMemberRole() == GroupMemberRoleEnum.OWNER.getCode()) {
            QueryWrapper<ImGroupMemberDAO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("app_id", appId)
                    .eq("group_id", groupId)
                    .eq("member_role", GroupMemberRoleEnum.OWNER.getCode());
            if (this.imGroupMemberMapper.selectCount(queryWrapper) > 0) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.GROUP_ALREADY_HAS_OWNER);
            }
        }
        QueryWrapper<ImGroupMemberDAO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", appId)
                .eq("group_id", groupId)
                .eq("member_id", groupMemberDTO.getMemberId());
        ImGroupMemberDAO groupMemberDAO = this.imGroupMemberMapper.selectOne(queryWrapper);
        if (groupMemberDAO != null && groupMemberDAO.getMemberRole() == GroupMemberRoleEnum.LEAVE.getCode()) {
            BeanUtils.copyProperties(groupMemberDTO, groupMemberDAO);
            groupMemberDAO.setJoinTime(System.currentTimeMillis());
            int updateResult = this.imGroupMemberMapper.updateById(groupMemberDAO);
            if (updateResult != 1) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.USER_JOIN_GROUP_ERROR);
            }
        } else if (groupMemberDAO == null) {
            groupMemberDAO = new ImGroupMemberDAO();
            BeanUtils.copyProperties(groupMemberDTO, groupMemberDAO);
            groupMemberDAO.setAppId(appId);
            groupMemberDAO.setGroupId(groupId);
            groupMemberDAO.setJoinTime(System.currentTimeMillis());
            int insertResult = this.imGroupMemberMapper.insert(groupMemberDAO);
            if (insertResult != 1) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.USER_JOIN_GROUP_ERROR);
            }
        } else {
            return ResponseVO.errorResponse(GroupErrorCodeEnum.USER_HAS_JOINED_GROUP);
        }
        return ResponseVO.successResponse();
    }

    @Override
    @Transactional
    public ResponseVO<?> inviteUserIntoGroup(InviteUserReq req) {
        ResponseVO<ImGroupDAO> response = this.imGroupService.getGroup(req.getAppId(), req.getGroupId());
        if (!response.isOk()) {
            return response;
        }
        ImGroupDAO groupInfo = response.getData();
        boolean isAdmin = false;
        /*
         * 私有群（private）	类似普通微信群，创建后仅支持已在群内的好友邀请加群，且无需被邀请方同意或群主审批
         * 公开群（Public）	类似 QQ 群，创建后群主可以指定群管理员，需要群主或管理员审批通过才能入群
         * 群类型 1私有群（类似微信） 2公开群(类似qq）
         */
        if (!isAdmin) {
            ResponseVO<ImGroupMemberDAO> operatorInfoResp =
                    this.getGroupMemberInfo(req.getAppId(), req.getGroupId(), req.getOperator());
            if (!operatorInfoResp.isOk()) {
                return operatorInfoResp;
            }
            boolean isManager = operatorInfoResp.getData().getMemberRole() == GroupMemberRoleEnum.MANAGER.getCode();
            boolean isOwner = operatorInfoResp.getData().getMemberRole() == GroupMemberRoleEnum.OWNER.getCode();
            if (groupInfo.getGroupType() == GroupTypeEnum.PUBLIC.getCode() && !(isManager || isOwner)) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.THIS_OPERATION_NEEDS_MANAGER_ROLE);
            }
        }
        InviteUserResp resp = new InviteUserResp();
        for (String userId : req.getInviteeIdList()) {
            ImGroupMemberDTO memberDTO = new ImGroupMemberDTO();
            memberDTO.setMemberId(userId);
            memberDTO.setMemberRole(GroupMemberRoleEnum.ORDINARY.getCode());
            ResponseVO<?> responseVO = null;
            try {
                responseVO = this.imGroupMemberService.addGroupMember(req.getAppId(), req.getGroupId(), memberDTO);
                if (!responseVO.isOk()) {
                    resp.getFailMemberItemList().add(new ImportGroupMemberResp.ResultItem(userId, responseVO.getMsg()));
                } else {
                    resp.getSuccessUserIdList().add(userId);
                }
            } catch (Exception e) {
                log.error(ERROR_MESSAGE, e);
                resp.getFailMemberItemList().add(
                        new ImportGroupMemberResp.ResultItem(userId, responseVO != null ? responseVO.getMsg() : ""));
            }
        }
        return ResponseVO.successResponse(resp);
    }

    @Override
    @Transactional
    public ResponseVO<?> exitGroup(ExitGroupReq req) {
        ResponseVO<ImUserDAO> responseVO = this.imUserService.getSingleUserInfo(req.getOperator(), req.getAppId());
        if (!responseVO.isOk()) {
            return responseVO;
        }
        ResponseVO<ImGroupDAO> response = this.imGroupService.getGroup(req.getAppId(), req.getGroupId());
        if (!response.isOk()) {
            return response;
        }
        if (response.getData().getGroupType() == GroupTypeEnum.PUBLIC.getCode() &&
                Objects.equals(response.getData().getOwnerId(), req.getOperator())) {
            return ResponseVO.errorResponse(GroupErrorCodeEnum.PUBLIC_GROUP_MUST_HAVE_OWNER);
        }
        return this.imGroupMemberService.leaveGroup(req.getAppId(), req.getGroupId(), req.getOperator());
    }

    @Override
    @Transactional
    public ResponseVO<?> removeMemberFromGroup(RemoveMemberReq req) {
        ResponseVO<ImUserDAO> operatorUserInfoResp = this.imUserService.getSingleUserInfo(req.getOperator(), req.getAppId());
        if (!operatorUserInfoResp.isOk()) {
            return operatorUserInfoResp;
        }
        ResponseVO<ImUserDAO> memberUserInfoResp = this.imUserService.getSingleUserInfo(req.getMemberId(), req.getAppId());
        if (!memberUserInfoResp.isOk()) {
            return memberUserInfoResp;
        }
        ResponseVO<ImGroupDAO> response = this.imGroupService.getGroup(req.getAppId(), req.getGroupId());
        if (!response.isOk()) {
            return response;
        }
        ImGroupDAO groupInfo = response.getData();
        boolean isAdmin = false;
        if (!isAdmin) {
            ResponseVO<ImGroupMemberDAO> operatorInfoResp =
                    this.getGroupMemberInfo(req.getAppId(), req.getGroupId(), req.getOperator());
            if (!operatorInfoResp.isOk()) {
                return operatorInfoResp;
            }
            ResponseVO<ImGroupMemberDAO> memberInfoResp =
                    this.getGroupMemberInfo(req.getAppId(), req.getGroupId(), req.getMemberId());
            if (!memberInfoResp.isOk()) {
                return memberInfoResp;
            }
            boolean isOperatorManager = operatorInfoResp.getData().getMemberRole() == GroupMemberRoleEnum.MANAGER.getCode();
            boolean isOperatorOwner = operatorInfoResp.getData().getMemberRole() == GroupMemberRoleEnum.OWNER.getCode();
            boolean isMemberManager = memberInfoResp.getData().getMemberRole() == GroupMemberRoleEnum.MANAGER.getCode();
            boolean isMemberOwner = memberInfoResp.getData().getMemberRole() == GroupMemberRoleEnum.OWNER.getCode();
            if (groupInfo.getGroupType() == GroupTypeEnum.PRIVATE.getCode() && !isOperatorOwner) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.THIS_OPERATION_NEEDS_OWNER_ROLE);
            }
            if (groupInfo.getGroupType() == GroupTypeEnum.PUBLIC.getCode() && !(isOperatorManager || isOperatorOwner)) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.THIS_OPERATION_NEEDS_MANAGER_ROLE);
            } else if (groupInfo.getGroupType() == GroupTypeEnum.PUBLIC.getCode() && isMemberOwner) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.CAN_NOT_REMOVE_GROUP_OWNER);
            } else if (groupInfo.getGroupType() == GroupTypeEnum.PUBLIC.getCode()
                    && isOperatorManager && isMemberManager) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.THIS_OPERATION_NEEDS_OWNER_ROLE);
            }
        }
        ResponseVO<?> responseVO = this.imGroupMemberService.leaveGroup(req.getAppId(), req.getGroupId(), req.getMemberId());
        if (!responseVO.isOk()) {
            return responseVO;
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> leaveGroup(Integer appId, String groupId, String memberId) {
        ResponseVO<ImGroupMemberDAO> memberInfoResp =
                this.getGroupMemberInfo(appId, groupId, memberId);
        if (!memberInfoResp.isOk()) {
            return memberInfoResp;
        }
        if (memberInfoResp.getData().getMemberRole() == GroupMemberRoleEnum.LEAVE.getCode()) {
            return ResponseVO.errorResponse(GroupErrorCodeEnum.USER_DID_NOT_JOIN_GROUP);
        }
        ImGroupMemberDAO groupDAO = new ImGroupMemberDAO();
        groupDAO.setId(memberInfoResp.getData().getId());
        groupDAO.setMemberRole(GroupMemberRoleEnum.LEAVE.getCode());
        groupDAO.setLeaveTime(System.currentTimeMillis());
        try {
            int updateResult = this.imGroupMemberMapper.updateById(groupDAO);
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
    public ResponseVO<ImGroupMemberDAO> getGroupMemberInfo(Integer appId, String groupId, String memberId) {
        QueryWrapper<ImGroupMemberDAO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", appId)
                .eq("group_id", groupId)
                .eq("member_id", memberId);
        ImGroupMemberDAO memberDAO = this.imGroupMemberMapper.selectOne(queryWrapper);
        if (memberDAO == null) {
            return ResponseVO.errorResponse(GroupErrorCodeEnum.USER_DID_NOT_JOIN_GROUP);
        }
        return ResponseVO.successResponse(memberDAO);
    }

    @Override
    public ResponseVO<List<ImGroupMemberDTO>> getGroupMemberList(Integer appId, String groupId) {
        List<ImGroupMemberDTO> groupMemberList;
        try {
            groupMemberList = this.imGroupMemberMapper.getGroupMemberList(appId, groupId);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e);
            return ResponseVO.errorResponse(GroupErrorCodeEnum.GET_GROUP_MEMBER_ERROR);
        }
        return ResponseVO.successResponse(groupMemberList);
    }

    @Override
    public ResponseVO<?> getGroupIdListBy(Integer appId, String userId) {
        ResponseVO<ImUserDAO> responseVO = this.imUserService.getSingleUserInfo(userId, appId);
        if (!responseVO.isOk()) {
            return responseVO;
        }
        List<String> groupIdList;
        try {
            groupIdList = this.imGroupMemberMapper.getGroupIdListBy(appId, userId);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e);
            return ResponseVO.errorResponse(GroupErrorCodeEnum.GET_JOINED_GROUP_ERROR);
        }
        return ResponseVO.successResponse(groupIdList);
    }

    @Override
    @Transactional
    public ResponseVO<?> updateGroupMemberInfo(Integer appId, String groupId, ImGroupMemberDTO groupMemberDTO) {
        QueryWrapper<ImGroupMemberDAO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", appId)
                .eq("group_id", groupId)
                .eq("member_id", groupMemberDTO.getMemberId());
        ImGroupMemberDAO groupMemberDAO = new ImGroupMemberDAO();
        BeanUtils.copyProperties(groupMemberDTO, groupMemberDAO);
        try {
            int updateResult = this.imGroupMemberMapper.update(groupMemberDAO, queryWrapper);
            if (updateResult != 1) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.UPDATE_GROUP_MEMBER_INFO_ERROR);
            }
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e);
            return ResponseVO.errorResponse(GroupErrorCodeEnum.UPDATE_GROUP_MEMBER_INFO_ERROR);
        }
        return ResponseVO.successResponse();
    }

    @Override
    @Transactional
    public ResponseVO<?> updateGroupMemberInfo(UpdateGroupMemberReq req) {
        ResponseVO<?> response = this.imGroupService.getGroup(req.getAppId(), req.getGroupId());
        if (!response.isOk()) {
            return response;
        }
        ImGroupDAO groupInfo = (ImGroupDAO) response.getData();
        // 检查被修改人是否在群内
        ResponseVO<ImGroupMemberDAO> memberInfoResp =
                this.getGroupMemberInfo(req.getAppId(), req.getGroupId(), req.getMemberId());
        if (!memberInfoResp.isOk()) {
            return memberInfoResp;
        }
        if (memberInfoResp.getData().getMemberRole() == GroupMemberRoleEnum.LEAVE.getCode()) {
            return ResponseVO.errorResponse(GroupErrorCodeEnum.USER_DID_NOT_JOIN_GROUP);
        }
        // 2. 权限控制
        boolean isAdmin = false;
        if (!isAdmin) {
            boolean isUpdateOneSelfInfo = Objects.equals(req.getOperator(), req.getMemberId());
            if (!StringUtils.isBlank(req.getAlias()) && !isUpdateOneSelfInfo) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.THIS_OPERATION_NEEDS_ONESELF);
            }
            if (req.getMemberRole() != null) {
                // 私有群不能设置管理员
                if (groupInfo.getGroupType() == GroupTypeEnum.PRIVATE.getCode() &&
                        (req.getMemberRole() == GroupMemberRoleEnum.MANAGER.getCode() ||
                                req.getMemberRole() == GroupMemberRoleEnum.OWNER.getCode())) {
                    return ResponseVO.errorResponse(GroupErrorCodeEnum.CAN_NOT_SET_MANAGER_IN_PRIVATE_GROUP);
                }

                // 获取操作人的成员信息
                ResponseVO<ImGroupMemberDAO> operatorMemberInfoResp =
                        this.getGroupMemberInfo(req.getAppId(), req.getGroupId(), req.getOperator());
                if (!operatorMemberInfoResp.isOk()) {
                    return operatorMemberInfoResp;
                }
                ImGroupMemberDAO operatorInfo = operatorMemberInfoResp.getData();
                boolean isOperatorManager = operatorInfo.getMemberRole() == GroupMemberRoleEnum.MANAGER.getCode();
                boolean isOperatorOwner = operatorInfo.getMemberRole() == GroupMemberRoleEnum.OWNER.getCode();
                if (!(isOperatorManager || isOperatorOwner)) {
                    return ResponseVO.errorResponse(GroupErrorCodeEnum.THIS_OPERATION_NEEDS_MANAGER_ROLE);
                }
                if (req.getMemberRole() == GroupMemberRoleEnum.MANAGER.getCode() && !isOperatorOwner) {
                    return ResponseVO.errorResponse(GroupErrorCodeEnum.THIS_OPERATION_NEEDS_OWNER_ROLE);
                }
            }
        }
        // 3. 更新操作
        ImGroupMemberDTO memberDTO = new ImGroupMemberDTO();
        memberDTO.setMemberId(req.getMemberId());
        if (StringUtils.isNotEmpty(req.getAlias())) {
            memberDTO.setAlias(req.getAlias());
        }
        if (req.getMemberRole() != null && req.getMemberRole() != GroupMemberRoleEnum.OWNER.getCode()) {
            memberDTO.setMemberRole(req.getMemberRole());
        }
        return this.imGroupMemberService.updateGroupMemberInfo(req.getAppId(), req.getGroupId(), memberDTO);
    }

    @Override
    public ResponseVO<?> muteGroupMember(MuteGroupMemberReq req) {
        ResponseVO<?> response = this.imGroupService.getGroup(req.getAppId(), req.getGroupId());
        if (!response.isOk()) {
            return response;
        }
        // 操作人信息
        ResponseVO<ImGroupMemberDAO> operatorInfoResp =
                this.getGroupMemberInfo(req.getAppId(), req.getGroupId(), req.getOperator());
        if (!operatorInfoResp.isOk()) {
            return operatorInfoResp;
        }
        ImGroupMemberDAO operatorInfo = operatorInfoResp.getData();
        if (operatorInfo.getMemberRole() == GroupMemberRoleEnum.LEAVE.getCode()) {
            return ResponseVO.errorResponse(GroupErrorCodeEnum.USER_DID_NOT_JOIN_GROUP);
        }
        // 被禁言人信息
        ResponseVO<ImGroupMemberDAO> memberInfoResp =
                this.getGroupMemberInfo(req.getAppId(), req.getGroupId(), req.getMemberId());
        if (!memberInfoResp.isOk()) {
            return memberInfoResp;
        }
        ImGroupMemberDAO memberInfo = memberInfoResp.getData();
        if (memberInfo.getMemberRole() == GroupMemberRoleEnum.LEAVE.getCode()) {
            return ResponseVO.errorResponse(GroupErrorCodeEnum.USER_DID_NOT_JOIN_GROUP);
        }
        boolean isAdmin = false;
        if (!isAdmin) {
            boolean isOperatorManager = operatorInfo.getMemberRole() == GroupMemberRoleEnum.MANAGER.getCode();
            boolean isOperatorOwner = operatorInfo.getMemberRole() == GroupMemberRoleEnum.OWNER.getCode();
            if (!(isOperatorManager || isOperatorOwner)) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.THIS_OPERATION_NEEDS_MANAGER_ROLE);
            }
            if (memberInfo.getMemberRole() == GroupMemberRoleEnum.OWNER.getCode()) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.THIS_OPERATION_NEEDS_APP_MANAGER_ROLE);
            }
            if (isOperatorManager && memberInfo.getMemberRole() == GroupMemberRoleEnum.MANAGER.getCode()) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.THIS_OPERATION_NEEDS_OWNER_ROLE);
            }
        }
        ImGroupMemberDAO memberDAO = new ImGroupMemberDAO();
        memberDAO.setId(memberInfo.getId());
        if (req.getSpeakDate() > 0) {
            memberDAO.setSpeakDate(System.currentTimeMillis() + req.getSpeakDate());
        } else {
            memberDAO.setSpeakDate(req.getSpeakDate());
        }
        try {
            int updateResult = this.imGroupMemberMapper.updateById(memberDAO);
            if (updateResult != 1) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.UPDATE_GROUP_MEMBER_INFO_ERROR);
            }
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e);
            return ResponseVO.errorResponse(GroupErrorCodeEnum.UPDATE_GROUP_MEMBER_INFO_ERROR);
        }
        return ResponseVO.successResponse();
    }
}
