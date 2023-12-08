package com.lq.im.service.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lq.im.common.ResponseVO;
import com.lq.im.common.enums.user.DelFlagEnum;
import com.lq.im.common.enums.user.UserErrorCodeEnum;
import com.lq.im.common.model.UserClientDTO;
import com.lq.im.service.config.HttpClientProperties;
import com.lq.im.service.callback.CallbackService;
import com.lq.im.service.user.mapper.ImUserMapper;
import com.lq.im.service.user.model.ImUserDAO;
import com.lq.im.service.user.model.req.*;
import com.lq.im.service.user.model.resp.GetUserInfoResp;
import com.lq.im.service.user.model.resp.ImportUserResp;
import com.lq.im.service.user.service.ImUserService;
import com.lq.im.service.utils.MessageQueueUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.lq.im.common.constant.Constants.CallbackCommand.AFTER_USER_INFO_MODIFIED;
import static com.lq.im.common.enums.user.UserCommand.USER_INFO_MODIFIED;

@Slf4j
@Service
public class ImUserServiceImpl implements ImUserService {
    public static final String ERROR_MESSAGE = "An error occurred";

    @Resource
    private ImUserMapper imUserMapper;
    @Resource
    private HttpClientProperties httpClientProperties;
    @Resource
    private CallbackService callbackService;
    @Resource
    private MessageQueueUtils messageQueueUtils;

    @Override
    public ResponseVO<?> importUser(ImportUserReq req) {
        if (req.getUserList() == null) {
            return ResponseVO.errorResponse(UserErrorCodeEnum.REQUEST_DATA_DOES_NOT_EXIST);
        }
        if (req.getUserList().size() > 100) {
            return ResponseVO.errorResponse(UserErrorCodeEnum.TOO_MUCH_DATA);
        }

        ImportUserResp resp = new ImportUserResp();
        req.getUserList().forEach(user -> {
            try {
                processImportedUser(user, req.getAppId(), resp);
            } catch (Exception e) {
                log.error(ERROR_MESSAGE, e);
                resp.getFailUserIdList().add(user.getUserId());
            }
        });
        return ResponseVO.successResponse(resp);
    }

    private void processImportedUser(ImUserDAO user, Integer appId, ImportUserResp resp) {
        user.setAppId(appId);
        int insertResult = this.imUserMapper.insert(user);
        if (insertResult == 1) {
            // 插入成功
            resp.getSuccessUserIdList().add(user.getUserId());
        } else {
            // 插入失败
            resp.getFailUserIdList().add(user.getUserId());
        }
    }

    @Override
    public ResponseVO<GetUserInfoResp> getUserInfo(GetUserInfoReq req) {
        GetUserInfoResp resp = new GetUserInfoResp();
        if (req == null || req.getUserIdList() == null || req.getUserIdList().isEmpty() || req.getAppId() == null) {
            return ResponseVO.successResponse(resp);
        }
        QueryWrapper<ImUserDAO> wrapper = new QueryWrapper<>();
        wrapper.in("user_id", req.getUserIdList())
                .eq("app_id", req.getAppId())
                .eq("del_flag", DelFlagEnum.NORMAL.getCode());
        List<ImUserDAO> userDAOList = this.imUserMapper.selectList(wrapper);
        resp.setUserList(userDAOList);
        Set<String> successUserIdSet = userDAOList.stream().map(ImUserDAO::getUserId).collect(Collectors.toSet());
        List<String> failUserIdList = req.getUserIdList().stream().filter(
                userId -> !successUserIdSet.contains(userId)
        ).collect(Collectors.toList());
        resp.setFailUserIdList(failUserIdList);
        return new ResponseVO<GetUserInfoResp>().success(resp);
    }

    @Override
    public ResponseVO<ImUserDAO> getSingleUserInfo(String userId, Integer appId) {
        QueryWrapper<ImUserDAO> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("app_id", appId)
                .eq("del_flag", DelFlagEnum.NORMAL.getCode());
        ImUserDAO imUserDAO = this.imUserMapper.selectOne(wrapper);
        if(imUserDAO == null) {
            return ResponseVO.errorResponse(UserErrorCodeEnum.USER_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(imUserDAO);
    }

    @Override
    public ResponseVO<?> deleteUser(DeleteUserReq req) {
        if (req.getUserIdList() == null) {
            return ResponseVO.errorResponse(UserErrorCodeEnum.REQUEST_DATA_DOES_NOT_EXIST);
        }
        ImUserDAO imUserDAO = new ImUserDAO();
        imUserDAO.setDelFlag(DelFlagEnum.DELETED.getCode());
        ImportUserResp resp = new ImportUserResp();
        req.getUserIdList().forEach(userId -> {
            QueryWrapper<ImUserDAO> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId)
                    .eq("app_id", req.getAppId())
                    .eq("del_flag", DelFlagEnum.NORMAL.getCode());

            try {
                int updateResult = this.imUserMapper.update(imUserDAO, wrapper);
                if (updateResult > 0) {
                    resp.getSuccessUserIdList().add(userId);
                } else {
                    resp.getFailUserIdList().add(userId);
                }
            } catch (Exception e) {
                log.error(ERROR_MESSAGE, e);
                resp.getFailUserIdList().add(userId);
            }
        });

        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<?> modifyUserInfo(ModifyUserInfoReq req) {
        QueryWrapper<ImUserDAO> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", req.getUserId())
                .eq("app_id", req.getAppId())
                .eq("del_flag", DelFlagEnum.NORMAL.getCode());
        ImUserDAO imUserDAO = this.imUserMapper.selectOne(wrapper);
        if(imUserDAO == null) {
            return ResponseVO.errorResponse(UserErrorCodeEnum.USER_IS_NOT_EXIST);
        }
        ImUserDAO userDAO = new ImUserDAO();
        BeanUtils.copyProperties(req, userDAO);
        userDAO.setUserId(null);
        userDAO.setAppId(null);
        int updateResult = this.imUserMapper.update(userDAO, wrapper);
        if (updateResult != 1) {
            return ResponseVO.errorResponse(UserErrorCodeEnum.MODIFY_USER_ERROR);
        }
        UserClientDTO userClient = new UserClientDTO(req.getAppId(), req.getClientType(), req.getUserId(), req.getImei());
        this.messageQueueUtils.sendMessage(USER_INFO_MODIFIED, req, userClient);
        if (this.httpClientProperties.isAfterUserInfoModified()) {
            this.callbackService.afterCallback(req.getAppId(), AFTER_USER_INFO_MODIFIED, JSONObject.toJSONString(req));
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> login(LoginReq req) {
        // todo 校验逻辑？
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> getUserSequence(GetUserSequenceReq req) {
        // todo
        return null;
    }
}
