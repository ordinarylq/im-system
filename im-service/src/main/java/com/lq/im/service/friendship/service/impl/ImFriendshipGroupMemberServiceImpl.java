package com.lq.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lq.im.service.friendship.mapper.ImFriendshipGroupMemberMapper;
import com.lq.im.service.friendship.model.ImFriendshipGroupMemberDAO;
import com.lq.im.service.friendship.service.ImFriendshipGroupMemberService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName: ImFriendshipGroupMemberServiceImpl
 * @Author: LiQi
 * @Date: 2023-05-31 16:48
 * @Version: V1.0
 * @Description:
 */
@Service
public class ImFriendshipGroupMemberServiceImpl implements ImFriendshipGroupMemberService {

    @Resource
    private ImFriendshipGroupMemberMapper imFriendshipGroupMemberMapper;

    @Override
    public Integer addGroupMember(Long groupId, String userId) {
        try {
            return this.imFriendshipGroupMemberMapper.insert(
                    new ImFriendshipGroupMemberDAO(groupId, userId));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Integer clearGroupMember(Long groupId) {
        try {
            QueryWrapper<ImFriendshipGroupMemberDAO> memberDAOQueryWrapper = new QueryWrapper<>();
            memberDAOQueryWrapper.eq("group_id", groupId);
            return this.imFriendshipGroupMemberMapper.delete(memberDAOQueryWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
