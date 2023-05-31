package com.lq.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lq.im.common.ResponseVO;
import com.lq.im.common.enums.ApproveFriendRequestStatusEnum;
import com.lq.im.common.enums.FriendShipErrorCodeEnum;
import com.lq.im.common.enums.FriendshipStatusEnum;
import com.lq.im.common.enums.ReadFriendshipRequestEnum;
import com.lq.im.service.friendship.mapper.ImFriendshipRequestMapper;
import com.lq.im.service.friendship.model.ImFriendshipDAO;
import com.lq.im.service.friendship.model.ImFriendshipRequestDAO;
import com.lq.im.service.friendship.model.req.ApproveFriendRequestReq;
import com.lq.im.service.friendship.model.req.FriendInfo;
import com.lq.im.service.friendship.model.req.GetAllFriendshipRequestReq;
import com.lq.im.service.friendship.model.req.ReadFriendshipRequestReq;
import com.lq.im.service.friendship.service.ImFriendshipRequestService;
import com.lq.im.service.friendship.service.ImFriendshipService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName: ImFriendshipRequestServiceImpl
 * @Author: LiQi
 * @Date: 2023-04-28 8:43
 * @Version: V1.0
 * @Description:
 */
@Service
public class ImFriendshipRequestServiceImpl implements ImFriendshipRequestService {

    @Resource
    private ImFriendshipRequestMapper imFriendshipRequestMapper;

    @Resource
    private ImFriendshipService imFriendshipService;

    @Override
    public ResponseVO addFriendRequest(Integer appId, String userId, FriendInfo friendInfo) {
        // 给申请表添加一条记录
        // 先查询看是否已存在
        QueryWrapper<ImFriendshipRequestDAO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", appId)
                .eq("from_id", userId)
                .eq("to_id", friendInfo.getFriendUserId());

        ImFriendshipRequestDAO imFriendshipRequestDAO = this.imFriendshipRequestMapper.selectOne(queryWrapper);
        if(imFriendshipRequestDAO != null) {
            // 如果已存在则更新字段：申请附加信息、更新时间
            Long id = imFriendshipRequestDAO.getId();
            imFriendshipRequestDAO = new ImFriendshipRequestDAO();
            imFriendshipRequestDAO.setId(id);
            if(StringUtils.isNotEmpty(friendInfo.getAddWording())) {
                imFriendshipRequestDAO.setAddWording(friendInfo.getAddWording());
            }
            if(StringUtils.isNotEmpty(friendInfo.getRemark())) {
                imFriendshipRequestDAO.setRemark(friendInfo.getRemark());
            }
            if(StringUtils.isNotEmpty(friendInfo.getAddSource())) {
                imFriendshipRequestDAO.setAddSource(friendInfo.getAddSource());
            }
            imFriendshipRequestDAO.setUpdateTime(System.currentTimeMillis());
            this.imFriendshipRequestMapper.updateById(imFriendshipRequestDAO);
        } else {
            // 如果不存在则插入一条数据
            imFriendshipRequestDAO = new ImFriendshipRequestDAO(null, appId, userId, friendInfo.getFriendUserId(),
                    0, friendInfo.getAddWording(), friendInfo.getRemark(), 0, System.currentTimeMillis(),
                    System.currentTimeMillis(), null, friendInfo.getAddSource());
            this.imFriendshipRequestMapper.insert(imFriendshipRequestDAO);
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO approveFriendRequest(ApproveFriendRequestReq req) {
        // 从数据库中查询是否有申请记录
        ImFriendshipRequestDAO imFriendshipRequestDAO = this.imFriendshipRequestMapper.selectById(req.getId());

        if(imFriendshipRequestDAO == null) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIEND_REQUEST_IS_NOT_EXIST);
        }
        // 判断该请求记录的接收人与操作人一致
        if(!req.getOperator().equals(imFriendshipRequestDAO.getFriendId())) {
            // 不一致则抛异常
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.NOT_APPROVER_OTHER_MAN_REQUEST);
        }

        // 更新审批结果
        ImFriendshipRequestDAO imFriendshipRequestDAO1 = new ImFriendshipRequestDAO();
        imFriendshipRequestDAO1.setId(req.getId());
        imFriendshipRequestDAO1.setApproveStatus(req.getApproveStatus());
        imFriendshipRequestDAO1.setUpdateTime(System.currentTimeMillis());

        this.imFriendshipRequestMapper.updateById(imFriendshipRequestDAO1);
        if(ApproveFriendRequestStatusEnum.AGREE.getCode() == req.getApproveStatus()) {
            // 如果同意，则进行添加好友操作
            FriendInfo friendInfo = new FriendInfo(
                    imFriendshipRequestDAO.getFriendId(),
                    imFriendshipRequestDAO.getRemark(),
                    FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode(),
                    FriendshipStatusEnum.BLACK_STATUS_NORMAL.getCode(),
                    imFriendshipRequestDAO.getAddSource(),
                    null,
                    imFriendshipRequestDAO.getAddWording());

            ResponseVO responseVO = this.imFriendshipService.doInternalAddFriend(imFriendshipRequestDAO.getUserId(),
                    friendInfo, req.getAppId());
            if(!responseVO.isOk() && responseVO.getCode() != FriendShipErrorCodeEnum.TO_IS_YOUR_FRIEND.getCode()) {
                return responseVO;
            }
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO readFriendshipRequest(ReadFriendshipRequestReq req) {
        QueryWrapper<ImFriendshipRequestDAO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId())
                .eq("to_id", req.getUserId());
        ImFriendshipRequestDAO imFriendshipRequestDAO = new ImFriendshipRequestDAO();
        imFriendshipRequestDAO.setReadStatus(ReadFriendshipRequestEnum.HAS_READ.getCode());
        imFriendshipRequestDAO.setUpdateTime(System.currentTimeMillis());

        this.imFriendshipRequestMapper.update(imFriendshipRequestDAO, queryWrapper);

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO getFriendshipRequest(GetAllFriendshipRequestReq req) {
        QueryWrapper<ImFriendshipRequestDAO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId())
                .eq("to_id", req.getUserId());

        List<ImFriendshipRequestDAO> imFriendshipRequestDAOList = this.imFriendshipRequestMapper.selectList(queryWrapper);

        return ResponseVO.successResponse(imFriendshipRequestDAOList);
    }
}
