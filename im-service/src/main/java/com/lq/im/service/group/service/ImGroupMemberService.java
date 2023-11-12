package com.lq.im.service.group.service;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.group.model.ImGroupMemberDAO;
import com.lq.im.service.group.model.req.ImGroupMemberDTO;
import com.lq.im.service.group.model.req.ImportGroupMemberReq;

import java.util.List;

public interface ImGroupMemberService {
    ResponseVO<?> importGroupMember(ImportGroupMemberReq req);

    ResponseVO<?> addGroupMember(Integer appId, String groupId, ImGroupMemberDTO groupMemberDTO);

    ResponseVO<ImGroupMemberDAO> getGroupMemberInfo(Integer appId, String groupId, String memberId);

    ResponseVO<List<ImGroupMemberDTO>> getGroupMemberList(Integer appId, String groupId);
}
