package com.ebeijia.zl.service.telrecharge.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ebeijia.zl.common.utils.enums.DataStatEnum;
import com.ebeijia.zl.facade.telrecharge.domain.CompanyInf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ebeijia.zl.facade.telrecharge.domain.ProviderInf;
import com.ebeijia.zl.service.telrecharge.mapper.ProviderInfMapper;
import com.ebeijia.zl.service.telrecharge.service.ProviderInfService;

/**
 *
 * 供应商信息 Service 实现类
 *
 * @User zhuqi
 * @Date 2018-12-10
 */
@Service
public class ProviderInfServiceImpl extends ServiceImpl<ProviderInfMapper, ProviderInf> implements ProviderInfService{

	@Override
	public boolean save(ProviderInf entity) {
		entity.setDataStat(DataStatEnum.TRUE_STATUS.getCode());
		entity.setCreateTime(System.currentTimeMillis());
		entity.setUpdateTime(System.currentTimeMillis());
		entity.setLockVersion(0);
		return super.save(entity);
	}

	@Override
	public boolean updateById(ProviderInf entity){
		entity.setUpdateTime(System.currentTimeMillis());
		return super.updateById(entity);
	}

	public int updateByDefaultRoute(){
		return baseMapper.updateByDefaultRoute();
	}
	
	public List<ProviderInf> getProviderInfList(ProviderInf providerInf){
		
		return baseMapper.getList(providerInf);
	}

	@Override
	public ProviderInf getProviderInfByLawCode(String lawCode) {
		return baseMapper.getProviderInfByLawCode(lawCode);
	}

	@Override
	public ProviderInf getProviderInfByOperSolr(Integer operSolr) {
		return baseMapper.getProviderInfByOperSolr(operSolr);
	}

	@Override
	public ProviderInf getProviderInfByProviderName(String providerName) {
		QueryWrapper<ProviderInf> queryWrapper = new QueryWrapper<ProviderInf>();
		queryWrapper.eq("provider_name", providerName);
		queryWrapper.eq("data_stat",DataStatEnum.TRUE_STATUS.getCode());
		return baseMapper.selectOne(queryWrapper);
	}
}
