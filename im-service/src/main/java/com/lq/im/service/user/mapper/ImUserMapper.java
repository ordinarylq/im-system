package com.lq.im.service.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lq.im.service.user.model.ImUserDAO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName: ImUserMapper
 * @Author: LiQi
 * @Date: 2023-04-11 14:13
 * @Version: V1.0
 */
@Mapper
public interface ImUserMapper extends BaseMapper<ImUserDAO> {
}
