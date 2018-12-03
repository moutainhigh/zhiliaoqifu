package com.cn.thinkx.ecom.basics.order.service.impl;

import com.cn.thinkx.ecom.basics.order.domain.OrderExpressPlatf;
import com.cn.thinkx.ecom.basics.order.mapper.OrderExpressPlatfMapper;
import com.cn.thinkx.ecom.basics.order.service.OrderExpressPlatfService;
import com.ebeijia.zl.common.core.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("orderExpressPlatfService")
public class OrderExpressPlatfServiceImpl extends BaseServiceImpl<OrderExpressPlatf> implements OrderExpressPlatfService{

	@Autowired
	private OrderExpressPlatfMapper orderExpressPlatfMapper;
	
	/**
	 * 保存订单物流关联表
	 * @param orderExpressPlatf
	 */
	public void saveOrderExpressPlatf(OrderExpressPlatf orderExpressPlatf){
		OrderExpressPlatf express=orderExpressPlatfMapper.getOrderExpressPlatfByPackId(orderExpressPlatf.getPackId(), 
																								orderExpressPlatf.getoItemId(),
																								orderExpressPlatf.getSkuCode());
		if(express ==null){
			orderExpressPlatfMapper.insert(orderExpressPlatf);
		}
	}
}
