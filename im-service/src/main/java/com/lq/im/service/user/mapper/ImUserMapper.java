package com.lq.im.service.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lq.im.service.user.model.ImUserDAO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImUserMapper extends BaseMapper<ImUserDAO> {
}
