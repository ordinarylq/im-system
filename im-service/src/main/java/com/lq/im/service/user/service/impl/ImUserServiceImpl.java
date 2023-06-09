package com.lq.im.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lq.im.common.ResponseVO;
import com.lq.im.common.enums.DelFlagEnum;
import com.lq.im.common.enums.UserErrorCodeEnum;
import com.lq.im.service.user.mapper.ImUserMapper;
import com.lq.im.service.user.model.ImUserDAO;
import com.lq.im.service.user.model.req.*;
import com.lq.im.service.user.model.resp.GetUserInfoResp;
import com.lq.im.service.user.model.resp.ImportUserResp;
import com.lq.im.service.user.service.ImUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName: ImUserServiceImpl
 * @Author: LiQi
 * @Date: 2023-04-11 11:33
 * @Version: V1.0
 * @Description:
 */
@Service
public class ImUserServiceImpl implements ImUserService {

    @Resource
    private ImUserMapper imUserMapper;

    @Override
    public ResponseVO importUser(ImportUserReq req) {
        if (req.getUserList() == null) {
            return ResponseVO.errorResponse(UserErrorCodeEnum.REQUEST_DATA_IS_NOT_EXIST);
        }
        if (req.getUserList().size() > 100) {
            return ResponseVO.errorResponse(UserErrorCodeEnum.IMPORT_SIZE_BEYOND);
        }

        ImportUserResp resp = new ImportUserResp();

        req.getUserList().forEach(user -> {
            try {
                user.setAppId(req.getAppId());
                int insertResult = this.imUserMapper.insert(user);
                if (insertResult == 1) {
                    // 插入成功
                    resp.getSuccessUserIdList().add(user.getUserId());
                } else {
                    // 插入失败
                    resp.getFailUserIdList().add(user.getUserId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                resp.getFailUserIdList().add(user.getUserId());
            }
        });

        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<GetUserInfoResp> getUserInfo(GetUserInfoReq req) {
        GetUserInfoResp resp = new GetUserInfoResp();
        if (req.getUserIdList() == null || req.getUserIdList().size() == 0) {
            return ResponseVO.successResponse(resp);
        }

        QueryWrapper<ImUserDAO> wrapper = new QueryWrapper<>();
        wrapper.in("user_id", req.getUserIdList())
                .eq("app_id", req.getAppId())
                .eq("del_flag", DelFlagEnum.NORMAL.getCode());
        List<ImUserDAO> userDAOList = this.imUserMapper.selectList(wrapper);
        resp.setUserList(userDAOList);

        Set<String> successUserIdSet = userDAOList.stream().map(ImUserDAO::getUserId).collect(Collectors.toSet());

        List<String> failUserIdList = req.getUserIdList().stream().filter(userId -> !successUserIdSet.contains(userId)).collect(Collectors.toList());
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
    public ResponseVO deleteUser(DeleteUserReq req) {
        if (req.getUserIdList() == null) {
            return ResponseVO.errorResponse(UserErrorCodeEnum.REQUEST_DATA_IS_NOT_EXIST);
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
                e.printStackTrace();
                resp.getFailUserIdList().add(userId);
            }
        });

        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO modifyUserInfo(ModifyUserInfoReq req) {
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

        // todo 发送更新后的用户信息到客户端
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO login(LoginReq req) {
        // todo 校验逻辑？
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO getUserSequence(GetUserSequenceReq req) {
        // todo
        return null;
    }
}
