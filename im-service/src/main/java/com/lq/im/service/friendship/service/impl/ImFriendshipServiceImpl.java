package com.lq.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lq.im.common.ResponseVO;
import com.lq.im.common.enums.AddFriendshipEnum;
import com.lq.im.common.enums.FriendShipErrorCodeEnum;
import com.lq.im.common.enums.FriendshipCheckEnum;
import com.lq.im.common.enums.FriendshipStatusEnum;
import com.lq.im.service.friendship.mapper.ImFriendshipMapper;
import com.lq.im.service.friendship.model.ImFriendshipDAO;
import com.lq.im.service.friendship.model.req.*;
import com.lq.im.service.friendship.model.resp.CheckFriendshipResp;
import com.lq.im.service.friendship.model.resp.ImportBlocklistResp;
import com.lq.im.service.friendship.model.resp.ImportFriendshipResp;
import com.lq.im.service.friendship.service.ImFriendshipRequestService;
import com.lq.im.service.friendship.service.ImFriendshipService;
import com.lq.im.service.user.model.ImUserDAO;
import com.lq.im.service.user.service.ImUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.lq.im.service.user.service.impl.ImUserServiceImpl.ERROR_MESSAGE;

@SuppressWarnings("DuplicatedCode")
@Service
@Slf4j
public class ImFriendshipServiceImpl implements ImFriendshipService {

    @Resource
    private ImFriendshipMapper imFriendshipMapper;

    @Resource
    private ImUserService imUserService;

    @Resource
    private ImFriendshipRequestService imFriendshipRequestService;

    @Override
    public ResponseVO<?> importFriendship(ImportFriendshipReq req) {
        if (req.getFriendInfoList() == null) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.REQUEST_DATA_DOES_NOT_EXIST);
        }
        if (req.getFriendInfoList().size() > 100) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.TOO_MUCH_DATA);
        }
        ImportFriendshipResp resp = new ImportFriendshipResp();
        for (FriendInfo friendInfo : req.getFriendInfoList()) {
            try {
                addOneFriendship(req.getAppId(), req.getUserId(), friendInfo, resp);
            } catch (Exception e) {
                log.error(ERROR_MESSAGE, e);
                resp.getFailFriendIdList().add(friendInfo.getFriendUserId());
            }
        }
        return ResponseVO.successResponse(resp);
    }
    private void addOneFriendship(Integer appId, String userId, FriendInfo friendInfo, ImportFriendshipResp resp) {
        ImFriendshipDAO imFriendshipDAO = new ImFriendshipDAO();
        imFriendshipDAO.setAppId(appId);
        imFriendshipDAO.setUserId(userId);
        BeanUtils.copyProperties(friendInfo, imFriendshipDAO);
        imFriendshipDAO.setCreateTime(System.currentTimeMillis());
        int insertResult = this.imFriendshipMapper.insert(imFriendshipDAO);
        if (insertResult == 1) {
            resp.getSuccessFriendIdList().add(friendInfo.getFriendUserId());
        } else {
            resp.getFailFriendIdList().add(friendInfo.getFriendUserId());
        }
    }

    @Override
    public ResponseVO<?> addFriendship(AddFriendshipReq req) {
        // 1. 判断这两个用户是否存在
        ResponseVO<ImUserDAO> friendUserInfo = checkIfTwoUsersExist(req.getAppId(), req.getUserId(), req.getFriendInfo().getFriendUserId());
        if (!friendUserInfo.isOk()) {
            return friendUserInfo;
        }
        // 2. 添加
        if(Objects.equals(friendUserInfo.getData().getFriendAllowType(), AddFriendshipEnum.NO_NEED_TO_CONFIRM.getCode())) {
            // 不需要确认
            return doInternalAddFriend(req.getUserId(), req.getFriendInfo(), req.getAppId());
        } else {
            // 需要确认
            // 先查是否已经是好友关系了
            QueryWrapper<ImFriendshipDAO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("app_id", req.getAppId())
                    .eq("from_id", req.getUserId())
                    .eq("to_id", req.getFriendInfo().getFriendUserId());
            ImFriendshipDAO imFriendshipDAO = this.imFriendshipMapper.selectOne(queryWrapper);
            if(imFriendshipDAO == null || imFriendshipDAO.getStatus() != FriendshipStatusEnum.BLOCK_STATUS_NORMAL.getCode()) {
                // 不是则添加一条申请
                return this.imFriendshipRequestService.addFriendRequest(req.getAppId(), req.getUserId(), req.getFriendInfo());
            } else {
                // 如果是则抛异常
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.OTHER_PERSON_IS_YOUR_FRIEND);
            }
        }
    }

    private ResponseVO<ImUserDAO> checkIfTwoUsersExist(Integer appId, String oneUserId, String friendUserId) {
        ResponseVO<ImUserDAO> oneUserInfo = imUserService.getSingleUserInfo(oneUserId, appId);
        if (!oneUserInfo.isOk()) {
            return oneUserInfo;
        }
        ResponseVO<ImUserDAO> friendUserInfo = imUserService.getSingleUserInfo(friendUserId, appId);
        if (!friendUserInfo.isOk()) {
            return friendUserInfo;
        }
        return friendUserInfo;
    }

    @Transactional
    public ResponseVO<?> doInternalAddFriend(String userId, FriendInfo friendInfo, Integer appId) {
        // a添加b
        ResponseVO<?> responseVO = addFriendOneWay(userId, friendInfo, appId);
        if (responseVO != null) {
            return responseVO;
        }
        // b添加a
        FriendInfo anotherFriendInfo = new FriendInfo(userId, null,
                FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode(), FriendshipStatusEnum.BLOCK_STATUS_NORMAL.getCode(),
                friendInfo.getAddSource(), null, null);
        ResponseVO<?> responseVO1 = addFriendOneWay(friendInfo.getFriendUserId(), anotherFriendInfo, appId);
        if (responseVO1 != null) {
            return responseVO1;
        }
        return ResponseVO.successResponse();
    }

    private ResponseVO<?> addFriendOneWay(String userId, FriendInfo friendInfo, Integer appId) {
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
            imFriendshipDAO.setBlack(FriendshipStatusEnum.BLOCK_STATUS_NORMAL.getCode());
            int insertResult = this.imFriendshipMapper.insert(imFriendshipDAO);
            if (insertResult != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.ADD_FRIEND_ERROR);
            }
        } else {
            // 如果已添加，则查看状态，若非正常，则更新，否则抛异常
            if (imFriendshipDAO.getStatus() == FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode()
                    && imFriendshipDAO.getBlack() == FriendshipStatusEnum.BLOCK_STATUS_NORMAL.getCode()) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.OTHER_PERSON_IS_YOUR_FRIEND);
            } else {
                UpdateWrapper<ImFriendshipDAO> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("app_id", appId)
                        .eq("from_id", userId)
                        .eq("to_id", friendInfo.getFriendUserId());
                ImFriendshipDAO friendshipDAO = new ImFriendshipDAO();
                friendshipDAO.setStatus(FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
                friendshipDAO.setBlack(FriendshipStatusEnum.BLOCK_STATUS_NORMAL.getCode());
                friendshipDAO.setRemark(friendInfo.getRemark());
                friendshipDAO.setAddSource(friendInfo.getAddSource());
                friendshipDAO.setExtra(friendInfo.getExtra());
                int updateResult = this.imFriendshipMapper.update(friendshipDAO, updateWrapper);
                if(updateResult != 1) {
                    return ResponseVO.errorResponse(FriendShipErrorCodeEnum.ADD_FRIEND_ERROR);
                }
            }
        }
        return null;
    }

    @Override
    public ResponseVO<?> updateFriendship(UpdateFriendshipReq req) {
        ResponseVO<ImUserDAO> responseVO = checkIfTwoUsersExist(req.getAppId(), req.getUserId(),
                req.getFriendInfo().getFriendUserId());
        if (!responseVO.isOk()) {
            return responseVO;
        }
        return doInternalUpdateFriendship(req.getUserId(), req.getFriendInfo(), req.getAppId());
    }

    @Transactional
    public ResponseVO<?> doInternalUpdateFriendship(String userId, FriendInfo friendInfo, Integer appId) {
        UpdateWrapper<ImFriendshipDAO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set(!StringUtils.isEmpty(friendInfo.getRemark()), "remark", friendInfo.getRemark())
                .set(!StringUtils.isEmpty(friendInfo.getAddSource()), "add_source", friendInfo.getAddSource())
                .set(!StringUtils.isEmpty(friendInfo.getExtra()), "extra", friendInfo.getExtra())
                .eq("app_id", appId)
                .eq("from_id", userId)
                .eq("to_id", friendInfo.getFriendUserId());
        int updateResult = this.imFriendshipMapper.update(null, updateWrapper);
        if(updateResult != 1) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.UPDATE_FRIENDSHIP_ERROR);
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> deleteFriendship(DeleteFriendshipReq req) {
        // 1. 查询两个用户是否存在
        ResponseVO<ImUserDAO> responseVO = checkIfTwoUsersExist(req.getAppId(), req.getUserId(), req.getFriendUserId());
        if (!responseVO.isOk()) {
            return responseVO;
        }
        // 2. 获取a-b的关系
        QueryWrapper<ImFriendshipDAO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId())
                .eq("from_id", req.getUserId())
                .eq("to_id", req.getFriendUserId());
        ImFriendshipDAO imFriendshipDAO = this.imFriendshipMapper.selectOne(queryWrapper);
        // 2.1 若没有关系则返回不是好友
        if(imFriendshipDAO == null) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.OTHER_PERSON_IS_NOT_YOUR_FRIEND);
        }
        // 2.2 若有关系且状态为正常，则更新状态为删除
        if(imFriendshipDAO.getStatus() != null &&
                imFriendshipDAO.getStatus() == FriendshipStatusEnum.BLOCK_STATUS_NORMAL.getCode()) {

            ImFriendshipDAO imFriendshipDAO1 = new ImFriendshipDAO();
            imFriendshipDAO1.setStatus(FriendshipStatusEnum.FRIEND_STATUS_DELETE.getCode());
            int updateResult = this.imFriendshipMapper.update(imFriendshipDAO1, queryWrapper);
            if(updateResult != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.DELETE_FRIENDSHIP_ERROR);
            }
        } else {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIEND_IS_DELETED);
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> deleteAllFriendship(String userId, Integer appId) {
        QueryWrapper<ImFriendshipDAO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", appId)
                .eq("from_id", userId)
                .eq("status", FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

        ImFriendshipDAO imFriendshipDAO = new ImFriendshipDAO();
        imFriendshipDAO.setStatus(FriendshipStatusEnum.FRIEND_STATUS_DELETE.getCode());
        this.imFriendshipMapper.update(imFriendshipDAO, queryWrapper);
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> getAllFriendship(GetAllFriendshipReq req) {
        ResponseVO<ImUserDAO> singleUserInfo = imUserService.getSingleUserInfo(req.getUserId(), req.getAppId());
        if (!singleUserInfo.isOk()) {
            return singleUserInfo;
        }
        QueryWrapper<ImFriendshipDAO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId())
                .eq("from_id", req.getUserId())
                .eq("status", FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
        return ResponseVO.successResponse(this.imFriendshipMapper.selectList(queryWrapper));
    }

    @Override
    public ResponseVO<?> getFriendship(GetFriendshipReq req) {
        ResponseVO<ImUserDAO> responseVO = checkIfTwoUsersExist(req.getAppId(), req.getUserId(), req.getFriendUserId());
        if (!responseVO.isOk()) {
            return responseVO;
        }
        QueryWrapper<ImFriendshipDAO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId())
                .eq("from_id", req.getUserId())
                .eq("to_id", req.getFriendUserId())
                .eq("status", FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
        ImFriendshipDAO imFriendshipDAO = this.imFriendshipMapper.selectOne(queryWrapper);
        if(imFriendshipDAO == null) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIENDSHIP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(imFriendshipDAO);
    }

    @Override
    public ResponseVO<?> checkFriendship(CheckFriendshipReq req) {
        ResponseVO<ImUserDAO> oneUserInfo = imUserService.getSingleUserInfo(req.getUserId(), req.getAppId());
        if (!oneUserInfo.isOk()) {
            return oneUserInfo;
        }
        List<CheckFriendshipResp> resp;
        if(Objects.equals(req.getCheckType(), FriendshipCheckEnum.SINGLE.getType())) {
            // 1-单向校验
            resp = this.imFriendshipMapper.singleCheckFriendshipStatus(req);
        } else {
            // 2-双向校验
            resp = this.imFriendshipMapper.bothCheckFriendshipStatus(req);
        }
        // 将不在im_friendship中的好友取出
        Set<String> respIdSet = resp.stream().map(CheckFriendshipResp::getToId).collect(Collectors.toSet());
        for (String friendId : req.getFriendIdList()) {
            if(!respIdSet.contains(friendId)) {
                CheckFriendshipResp checkFriendshipResp = new CheckFriendshipResp(req.getAppId(), req.getUserId(),
                        friendId, FriendshipStatusEnum.FRIEND_STATUS_NO_FRIEND.getCode());
                resp.add(checkFriendshipResp);
            }
        }
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<?> importBlocklist(ImportBlocklistReq req) {
        if(req.getFriendUserIdList() == null) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.REQUEST_DATA_DOES_NOT_EXIST);
        }
        if(req.getFriendUserIdList().size() > 100) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.TOO_MUCH_DATA);
        }
        ImportBlocklistResp resp = new ImportBlocklistResp();
        for (String friendUserId : req.getFriendUserIdList()) {
            QueryWrapper<ImFriendshipDAO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("app_id", req.getAppId())
                    .eq("from_id", req.getUserId())
                    .eq("to_id", friendUserId);
            ImportBlocklistResp.ResultItem resultItem = new ImportBlocklistResp.ResultItem();
            resultItem.setUserId(friendUserId);
            ImFriendshipDAO selectResult = this.imFriendshipMapper.selectOne(queryWrapper);
            if(selectResult == null) {
                resultItem.setCodeAndMessage(FriendShipErrorCodeEnum.FRIENDSHIP_IS_NOT_EXIST);
                resp.getFailList().add(friendUserId);
            } else if (selectResult.getBlack() == FriendshipStatusEnum.BLOCK_STATUS_BLOCKED.getCode()) {
                resultItem.setCodeAndMessage(FriendShipErrorCodeEnum.FRIEND_IS_BLOCKED);
                resp.getFailList().add(friendUserId);
            } else {
                // 更新拉黑状态
                ImFriendshipDAO imFriendshipDAO = new ImFriendshipDAO();
                imFriendshipDAO.setBlack(FriendshipStatusEnum.BLOCK_STATUS_BLOCKED.getCode());
                try {
                    int updateResult = this.imFriendshipMapper.update(imFriendshipDAO, queryWrapper);
                    if(updateResult == 1) {
                        resultItem.setResultCode(0);
                        resultItem.setResultMessage("");
                    } else {
                        resultItem.setCodeAndMessage(FriendShipErrorCodeEnum.BLOCK_FRIEND_ERROR);
                        resp.getFailList().add(friendUserId);
                    }
                } catch (Exception e) {
                    log.error(ERROR_MESSAGE, e);
                    resultItem.setCodeAndMessage(FriendShipErrorCodeEnum.BLOCK_FRIEND_ERROR);
                    resp.getFailList().add(friendUserId);
                }
            }
            resp.getResultList().add(resultItem);
        }
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<?> blockFriend(BlockFriendReq req) {
        // 1. 先查询两个用户是否存在
        ResponseVO<ImUserDAO> responseVO = imUserService.getSingleUserInfo(req.getUserId(), req.getAppId());
        if (!responseVO.isOk()) {
            return responseVO;
        }
        // 2. 再查询关系链
        QueryWrapper<ImFriendshipDAO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId())
                .eq("from_id", req.getUserId())
                .eq("to_id", req.getFriendUserId());
        ImFriendshipDAO imFriendshipDAO = this.imFriendshipMapper.selectOne(queryWrapper);
        if(imFriendshipDAO == null) {
            ImFriendshipDAO imFriendshipDAO1 = new ImFriendshipDAO(req.getAppId(), req.getUserId(), req.getFriendUserId(),
                    null, FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode(),
                    FriendshipStatusEnum.BLOCK_STATUS_BLOCKED.getCode(), null,System.currentTimeMillis(),
                    null, null, null);
            int insertResult = this.imFriendshipMapper.insert(imFriendshipDAO1);
            if(insertResult != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.BLOCK_FRIEND_ERROR);
            }
            return ResponseVO.successResponse();
        }
        // 3. 再判断block字段
        if(imFriendshipDAO.getBlack() == FriendshipStatusEnum.BLOCK_STATUS_BLOCKED.getCode()) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIEND_IS_BLOCKED);
        }
        // 更新block字段
        ImFriendshipDAO imFriendshipDAO1 = new ImFriendshipDAO();
        imFriendshipDAO1.setBlack(FriendshipStatusEnum.BLOCK_STATUS_BLOCKED.getCode());
        try {
            int updateResult = this.imFriendshipMapper.update(imFriendshipDAO1, queryWrapper);
            if(updateResult != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.BLOCK_FRIEND_ERROR);
            }
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e);
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.BLOCK_FRIEND_ERROR);
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> unblockFriend(UnblockFriendReq req) {
        // 1. 查询用户是否存在
        ResponseVO<ImUserDAO> responseVO = imUserService.getSingleUserInfo(req.getUserId(), req.getAppId());
        if (!responseVO.isOk()) {
            return responseVO;
        }
        // 2. 查询关系链
        QueryWrapper<ImFriendshipDAO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId())
                .eq("from_id", req.getUserId())
                .eq("to_id", req.getFriendUserId());
        ImFriendshipDAO imFriendshipDAO = this.imFriendshipMapper.selectOne(queryWrapper);
        if(imFriendshipDAO == null || imFriendshipDAO.getBlack() == FriendshipStatusEnum.BLOCK_STATUS_NORMAL.getCode()) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIEND_IS_NOT_BLOCKED);
        }
        ImFriendshipDAO imFriendshipDAO1 = new ImFriendshipDAO();
        imFriendshipDAO1.setBlack(FriendshipStatusEnum.BLOCK_STATUS_NORMAL.getCode());
        int updateResult = this.imFriendshipMapper.update(imFriendshipDAO1, queryWrapper);
        if(updateResult != 1) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.DELETE_BLOCK_LIST_FAIL);
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> checkBlocklist(CheckFriendshipReq req) {
        ResponseVO<ImUserDAO> oneUserInfo = imUserService.getSingleUserInfo(req.getUserId(), req.getAppId());
        if (!oneUserInfo.isOk()) {
            return oneUserInfo;
        }
        List<CheckFriendshipResp> resp;
        if(Objects.equals(req.getCheckType(), FriendshipCheckEnum.SINGLE.getType())) {
            // 1-单向校验
            resp = this.imFriendshipMapper.singleCheckBlacklist(req);
        } else {
            // 2-双向校验
            resp = this.imFriendshipMapper.bothCheckBlacklist(req);
        }
        // 将不在im_friendship中的好友取出
        Set<String> collect = resp.stream().map(CheckFriendshipResp::getToId).collect(Collectors.toSet());
        for (String userId : req.getFriendIdList()) {
            if(!collect.contains(userId)) {
                CheckFriendshipResp checkFriendshipResp = new CheckFriendshipResp(
                        req.getAppId(), req.getUserId(), userId, 0);
                resp.add(checkFriendshipResp);
            }
        }
        return ResponseVO.successResponse(resp);
    }
}
