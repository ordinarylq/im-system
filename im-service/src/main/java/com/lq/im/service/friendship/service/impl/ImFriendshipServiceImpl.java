package com.lq.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lq.im.common.ResponseVO;
import com.lq.im.common.enums.FriendShipErrorCodeEnum;
import com.lq.im.common.enums.FriendshipStatusEnum;
import com.lq.im.service.friendship.mapper.ImFriendshipMapper;
import com.lq.im.service.friendship.model.ImFriendshipDAO;
import com.lq.im.service.friendship.model.req.*;
import com.lq.im.service.friendship.model.resp.ImportFriendshipResp;
import com.lq.im.service.friendship.service.ImFriendshipService;
import com.lq.im.service.user.model.ImUserDAO;
import com.lq.im.service.user.service.ImUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @ClassName: ImFriendshipServiceImpl
 * @Author: LiQi
 * @Date: 2023-04-13 15:19
 * @Version: V1.0
 * @Description:
 */
@SuppressWarnings("DuplicatedCode")
@Service
public class ImFriendshipServiceImpl implements ImFriendshipService {

    @Resource
    private ImFriendshipMapper imFriendshipMapper;

    @Resource
    private ImUserService imUserService;


    @Override
    public ResponseVO importFriendship(ImportFriendshipReq req) {
        if (req.getFriendInfoList() == null) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.REQUEST_DATA_IS_NOT_EXIST);
        }
        if (req.getFriendInfoList().size() > 100) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.IMPORT_SIZE_BEYOND);
        }
        ImportFriendshipResp resp = new ImportFriendshipResp();
        /*
        // 首先判断a是否存在
        ResponseVO<ImUserDAO> userInfo = this.imUserService.getSingleUserInfo(req.getUserId(), req.getAppId());
        if (!userInfo.isOk()) {
            return userInfo;
        }


        for (ImportFriendshipReq.FriendInfo friend : req.getFriendInfoList()) {
            // 循环朋友列表，判断这个朋友是否存在
            ResponseVO<ImUserDAO> friendUserInfo = this.imUserService.getSingleUserInfo(friend.getFriendUserId(), req.getAppId());
            if (!friendUserInfo.isOk()) {
                // 如果不存在，添加到失败列表
                resp.getFailFriendIdList().add(friend.getFriendUserId());
                continue;
            }
            // 如果存在，判断用户和该朋友的当前关系
            QueryWrapper<ImFriendshipDAO> wrapper = new QueryWrapper<>();
            wrapper.eq("app_id", req.getAppId())
                    .eq("from_id", req.getUserId())
                    .eq("to_id", friend.getFriendUserId());
            ImFriendshipDAO friendshipDAO = this.imFriendshipMapper.selectOne(wrapper);

            ImFriendshipDAO imFriendshipDAO = new ImFriendshipDAO();
            BeanUtils.copyProperties(friend, imFriendshipDAO);
            imFriendshipDAO.setAppId(req.getAppId());
            imFriendshipDAO.setUserId(req.getUserId());

            if (friendshipDAO == null) {
                // 用户与该朋友没有建立好友关系
                // 添加好友关系
                imFriendshipDAO.setStatus(FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
                imFriendshipDAO.setBlack(FriendshipStatusEnum.BLACK_STATUS_NORMAL.getCode());
                imFriendshipDAO.setCreateTime(System.currentTimeMillis());

                try {
                    int insertResult = this.imFriendshipMapper.insert(imFriendshipDAO);
                    if (insertResult == 1) {
                        resp.getSuccessFriendIdList().add(friend.getFriendUserId());
                    } else {
                        resp.getFailFriendIdList().add(friend.getFriendUserId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    resp.getFailFriendIdList().add(friend.getFriendUserId());
                }
            } else {
                // 用户与该朋友已经建立好友关系
                // 更新状态
                UpdateWrapper<ImFriendshipDAO> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("app_id", req.getAppId())
                        .eq("from_id", req.getUserId())
                        .eq("to_id", friend.getFriendUserId());

                try {
                    int updateResult = this.imFriendshipMapper.update(imFriendshipDAO, updateWrapper);
                    if(updateResult == 1) {
                        resp.getSuccessFriendIdList().add(friend.getFriendUserId());
                    } else {
                        resp.getFailFriendIdList().add(friend.getFriendUserId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    resp.getFailFriendIdList().add(friend.getFriendUserId());
                }
            }
        }
         */
        for (FriendInfo friendInfo : req.getFriendInfoList()) {
            ImFriendshipDAO imFriendshipDAO = new ImFriendshipDAO();
            BeanUtils.copyProperties(friendInfo, imFriendshipDAO);
            imFriendshipDAO.setAppId(req.getAppId());
            imFriendshipDAO.setUserId(req.getUserId());

            try {
                int insertResult = this.imFriendshipMapper.insert(imFriendshipDAO);
                if (insertResult == 1) {
                    resp.getSuccessFriendIdList().add(friendInfo.getFriendUserId());
                } else {
                    resp.getFailFriendIdList().add(friendInfo.getFriendUserId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                resp.getFailFriendIdList().add(friendInfo.getFriendUserId());
            }
        }
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO addFriendship(AddFriendshipReq req) {
        // 先判断这两个用户是否存在
        ResponseVO<ImUserDAO> singleUserInfo = imUserService.getSingleUserInfo(req.getUserId(), req.getAppId());
        if (!singleUserInfo.isOk()) {
            return singleUserInfo;
        }

        ResponseVO<ImUserDAO> friendUserInfo = imUserService.getSingleUserInfo(
                req.getFriendInfo().getFriendUserId(), req.getAppId());
        if (!friendUserInfo.isOk()) {
            return friendUserInfo;
        }
        // 再添加
        return doInternalAddFriend(req.getUserId(), req.getFriendInfo(), req.getAppId());
    }

    @Transactional
    ResponseVO doInternalAddFriend(String userId, FriendInfo friendInfo, Integer appId) {
        // 先查a是否已添加b
        QueryWrapper<ImFriendshipDAO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", appId)
                .eq("from_id", userId)
                .eq("to_id", friendInfo.getFriendUserId());
        ImFriendshipDAO imFriendshipDAO = this.imFriendshipMapper.selectOne(queryWrapper);
        if (imFriendshipDAO == null) {
            // 如果未添加，则插入数据
            imFriendshipDAO = new ImFriendshipDAO();
            imFriendshipDAO.setAppId(appId);
            imFriendshipDAO.setUserId(userId);
            BeanUtils.copyProperties(friendInfo, imFriendshipDAO);
            imFriendshipDAO.setCreateTime(System.currentTimeMillis());
            imFriendshipDAO.setStatus(FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            imFriendshipDAO.setBlack(FriendshipStatusEnum.BLACK_STATUS_NORMAL.getCode());

            int insertResult = this.imFriendshipMapper.insert(imFriendshipDAO);
            if (insertResult != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.ADD_FRIEND_ERROR);
            }
        } else {
            // 如果已添加，则查看状态，若非正常，则更新，否则抛异常
            if (imFriendshipDAO.getStatus() == FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode()
            && imFriendshipDAO.getBlack() == FriendshipStatusEnum.BLACK_STATUS_NORMAL.getCode()) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.TO_IS_YOUR_FRIEND);
            } else {

                UpdateWrapper<ImFriendshipDAO> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("app_id", appId)
                        .eq("from_id", userId)
                        .eq("to_id", friendInfo.getFriendUserId());

                ImFriendshipDAO friendshipDAO = new ImFriendshipDAO();
                friendshipDAO.setStatus(FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
                friendshipDAO.setBlack(FriendshipStatusEnum.BLACK_STATUS_NORMAL.getCode());
                friendshipDAO.setRemark(friendInfo.getRemark());
                friendshipDAO.setAddSource(friendInfo.getAddSource());
                friendshipDAO.setExtra(friendInfo.getExtra());

                int updateResult = this.imFriendshipMapper.update(friendshipDAO, updateWrapper);
                if(updateResult != 1) {
                    return ResponseVO.errorResponse(FriendShipErrorCodeEnum.ADD_FRIEND_ERROR);
                }
            }
        }

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO updateFriendship(UpdateFriendshipReq req) {
        // todo 抽离公共部分
        // 先判断这两个用户是否存在
        ResponseVO<ImUserDAO> singleUserInfo = imUserService.getSingleUserInfo(req.getUserId(), req.getAppId());
        if (!singleUserInfo.isOk()) {
            return singleUserInfo;
        }

        ResponseVO<ImUserDAO> friendUserInfo = imUserService.getSingleUserInfo(
                req.getFriendInfo().getFriendUserId(), req.getAppId());
        if (!friendUserInfo.isOk()) {
            return friendUserInfo;
        }

        return doInternalUpdateFriendship(req.getUserId(), req.getFriendInfo(), req.getAppId());
    }

    @Transactional
    ResponseVO doInternalUpdateFriendship(String userId, FriendInfo friendInfo, Integer appId) {

        UpdateWrapper<ImFriendshipDAO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set(!StringUtils.isEmpty(friendInfo.getRemark()), "remark", friendInfo.getRemark())
                .set(!StringUtils.isEmpty(friendInfo.getAddSource()), "add_source", friendInfo.getAddSource())
                .set(!StringUtils.isEmpty(friendInfo.getExtra()), "extra", friendInfo.getExtra())
                .eq("app_id", appId)
                .eq("from_id", userId)
                .eq("to_id", friendInfo.getFriendUserId());
        int updateResult = this.imFriendshipMapper.update(null, updateWrapper);
        if(updateResult != 1) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.UPDATE_FRIENDSHIP_FAIL);
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO deleteFriendship(DeleteFriendshipReq req) {
        // 1. 查询两个用户是否存在
        ResponseVO<ImUserDAO> singleUserInfo = imUserService.getSingleUserInfo(req.getUserId(), req.getAppId());
        if (!singleUserInfo.isOk()) {
            return singleUserInfo;
        }

        ResponseVO<ImUserDAO> friendUserInfo = imUserService.getSingleUserInfo(req.getFriendUserId(), req.getAppId());
        if (!friendUserInfo.isOk()) {
            return friendUserInfo;
        }

        // 2. 获取a-b的关系
        QueryWrapper<ImFriendshipDAO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId())
                .eq("from_id", req.getUserId())
                .eq("to_id", req.getFriendUserId());
        ImFriendshipDAO imFriendshipDAO = this.imFriendshipMapper.selectOne(queryWrapper);
        // 2.1 若没有关系则返回不是好友
        if(imFriendshipDAO == null) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.TO_IS_NOT_YOUR_FRIEND);
        }
        // 2.2 若有关系且状态为正常，则更新状态为删除
        if(imFriendshipDAO.getStatus() != null &&
                imFriendshipDAO.getStatus() == FriendshipStatusEnum.BLACK_STATUS_NORMAL.getCode()) {

            ImFriendshipDAO imFriendshipDAO1 = new ImFriendshipDAO();
            imFriendshipDAO1.setStatus(FriendshipStatusEnum.FRIEND_STATUS_DELETE.getCode());
            int updateResult = this.imFriendshipMapper.update(imFriendshipDAO1, queryWrapper);
            if(updateResult != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.DELETE_FRIENDSHIP_FAIL);
            }

        } else {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIEND_IS_DELETED);
        }

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO deleteAllFriendship(String userId, Integer appId) {
        QueryWrapper<ImFriendshipDAO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", appId)
                .eq("from_id", userId)
                .eq("status", FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

        ImFriendshipDAO imFriendshipDAO = new ImFriendshipDAO();
        imFriendshipDAO.setStatus(FriendshipStatusEnum.FRIEND_STATUS_DELETE.getCode());

        this.imFriendshipMapper.update(imFriendshipDAO, queryWrapper);

        return ResponseVO.successResponse();
    }
}
