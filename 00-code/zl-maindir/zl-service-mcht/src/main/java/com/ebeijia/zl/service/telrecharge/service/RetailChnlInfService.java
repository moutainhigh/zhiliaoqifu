package com.ebeijia.zl.service.telrecharge.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ebeijia.zl.facade.telrecharge.domain.RetailChnlInf;


/**
 *
 * 分销商信息表 Service 接口类
 *
 * @User zhuqi
 * @Date 2018-12-10
 */
public interface RetailChnlInfService extends IService<RetailChnlInf> {

	List<RetailChnlInf> getList(RetailChnlInf retailChnlInf);

	RetailChnlInf getRetailChnlInfByLawCode(String lawCode);

	/**
	 * 根据分销商名称查询分销商信息
	 * @param channelName
	 * @return
	 * @throws Exception
	 */
	RetailChnlInf getRetailChnlInfByChannelName(String channelName) throws Exception;
}
