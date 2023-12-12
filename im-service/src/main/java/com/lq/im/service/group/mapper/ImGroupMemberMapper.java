package com.lq.im.service.group.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lq.im.service.group.model.ImGroupMemberDAO;
import com.lq.im.service.group.model.req.ImGroupMemberDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ImGroupMemberMapper extends BaseMapper<ImGroupMemberDAO> {

    @Select("select m.member_id, m.speak_date, m.member_role, m.alias, m.join_time, m.join_type " +
            "from im_group_member m " +
            "where m.app_id = #{appId} and m.group_id = #{groupId} "
    )
    List<ImGroupMemberDTO> getGroupMemberList(@Param("appId") Integer appId, @Param("groupId") String groupId);

    @Select("select m.group_id " +
            "from im_group_member m " +
            "where m.app_id = #{appId} and m.member_id = #{memberId} and m.member_role != 3"
    )
    List<String> getGroupIdListBy(@Param("appId") Integer appId, @Param("memberId") String memberId);

    @Select("select m.member_id, m.speak_date, m.member_role, m.alias, m.join_time, m.join_type " +
            "from im_group_member m " +
            "where m.app_id = #{appId} and m.group_id = #{groupId} and m.member_role in (1, 2) "
    )
    List<ImGroupMemberDTO> getGroupManagerList(@Param("appId") Integer appId, @Param("groupId") String groupId);
}
