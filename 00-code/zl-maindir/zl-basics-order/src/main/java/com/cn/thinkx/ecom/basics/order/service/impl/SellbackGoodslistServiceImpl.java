package com.cn.thinkx.ecom.basics.order.service.impl;

import com.cn.thinkx.ecom.basics.order.domain.SellbackGoodslist;
import com.cn.thinkx.ecom.basics.order.mapper.SellbackGoodslistMapper;
import com.cn.thinkx.ecom.basics.order.service.SellbackGoodslistService;
import com.ebeijia.zl.common.core.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("sellbackGoodslistService")
public class SellbackGoodslistServiceImpl extends BaseServiceImpl<SellbackGoodslist> implements SellbackGoodslistService {

	@Autowired
	private SellbackGoodslistMapper sellbackGoodslistMapper;
	
	@Override
	public List<SellbackGoodslist> getSellbackGoodslistList(SellbackGoodslist sellbackGoodslist) {
		return sellbackGoodslistMapper.getSellbackGoodslistList(sellbackGoodslist);
	}

	@Override
	public List<SellbackGoodslist> getSellbackGoodslistBy(SellbackGoodslist sellbackGoodslist) {
		return sellbackGoodslistMapper.getSellbackGoodslistBy(sellbackGoodslist);
	}

	@Override
	public void updateSellbackGoodslistByReturnsId(SellbackGoodslist sellbackGoodslist) {
		
		sellbackGoodslistMapper.updateSellbackGoodslistByReturnsId(sellbackGoodslist);
		
	}


}
