package com.ebeijia.zl.service.account.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ebeijia.zl.facade.account.dto.IntfaceTransLog;
import org.apache.ibatis.annotations.Param;

/**
 *
 * itf接口平台流水表 Mapper 接口
 *
 * @User zhuqi
 * @Date 2018-11-30
 */
@Mapper
public interface IntfaceTransLogMapper extends BaseMapper<IntfaceTransLog> {

    IntfaceTransLog getItfTransLogDmsChannelTransId(@Param("dmsRelatedKey")String dmsRelatedKey,@Param("transChnl")String transChnl);
}
