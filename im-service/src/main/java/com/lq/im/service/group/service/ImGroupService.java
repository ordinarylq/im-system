package com.lq.im.service.group.service;

import com.lq.im.common.ResponseVO;
import com.lq.im.service.group.model.ImGroupDAO;
import com.lq.im.service.group.model.req.CreateGroupReq;
import com.lq.im.service.group.model.req.ImportGroupReq;
import com.lq.im.service.group.model.req.UpdateGroupInfoReq;

public interface ImGroupService {
    ResponseVO<?> importGroup(ImportGroupReq req);

    ResponseVO<?> createGroup(CreateGroupReq req);

    /**
     * 更新群组信息。
     * 需要进行权限控制：如果不是后台调用，对于公开群，只有管理员或者群主可以执行更新操作。
     */
    ResponseVO<?> updateGroupInfo(UpdateGroupInfoReq req);
    ResponseVO<ImGroupDAO> getGroup(Integer appId, String groupId);

    ResponseVO<?> getGroupWithMemberList(Integer appId, String groupId);
}
