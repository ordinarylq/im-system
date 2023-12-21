package com.lq.im.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lq.im.message.model.ImMessageHistoryDAO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

@Mapper
public interface ImMessageHistoryMapper extends BaseMapper<ImMessageHistoryDAO> {

    Integer insertBatchSomeColumn(Collection<ImMessageHistoryDAO> entityList);

}
