package com.ebeijia.zl.service.telrecharge.service.impl;

import java.util.List;

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
	
	@Autowired
	private ProviderInfMapper providerInfMapper;

	public int updateByDefaultRoute(){
		return providerInfMapper.updateByDefaultRoute();
	}
	
	public List<ProviderInf> getProviderInfList(ProviderInf providerInf){
		
		return providerInfMapper.getList(providerInf);
	}
}