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
}
