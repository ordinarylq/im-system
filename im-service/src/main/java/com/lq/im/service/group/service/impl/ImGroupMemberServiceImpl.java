package com.lq.im.service.group.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lq.im.common.ResponseVO;
import com.lq.im.common.enums.GroupErrorCodeEnum;
import com.lq.im.common.enums.GroupMemberRoleEnum;
import com.lq.im.service.group.mapper.ImGroupMemberMapper;
import com.lq.im.service.group.model.ImGroupMemberDAO;
import com.lq.im.service.group.model.req.ImGroupMemberDTO;
import com.lq.im.service.group.model.req.ImportGroupMemberReq;
import com.lq.im.service.group.model.resp.ImportGroupMemberResp;
import com.lq.im.service.group.service.ImGroupMemberService;
import com.lq.im.service.group.service.ImGroupService;
import com.lq.im.service.user.model.ImUserDAO;
import com.lq.im.service.user.service.ImUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;

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
}
