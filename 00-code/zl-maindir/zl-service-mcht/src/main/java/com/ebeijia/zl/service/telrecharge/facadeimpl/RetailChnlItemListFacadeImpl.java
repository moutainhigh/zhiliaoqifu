package com.ebeijia.zl.service.telrecharge.facadeimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ebeijia.zl.common.utils.tools.StringUtil;
import com.ebeijia.zl.facade.telrecharge.domain.RetailChnlItemList;
import com.ebeijia.zl.facade.telrecharge.service.RetailChnlItemListFacade;
import com.ebeijia.zl.service.telrecharge.enums.TelRechargeConstants;
import com.ebeijia.zl.service.telrecharge.service.RetailChnlItemListService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.context.annotation.Configuration;

@Configuration
@com.alibaba.dubbo.config.annotation.Service()
public class RetailChnlItemListFacadeImpl implements RetailChnlItemListFacade {

	@Autowired
	private RetailChnlItemListService retailChnlItemListService;

	@Override
	public RetailChnlItemList getRetailChnlItemListById(String id) throws Exception {
		return retailChnlItemListService.getById(id);
	}

	@Override
	public boolean saveRetailChnlItemList(RetailChnlItemList retailChnlItemList) throws Exception {
		return retailChnlItemListService.save(retailChnlItemList);
	}

	@Override
	public boolean updateRetailChnlItemList(RetailChnlItemList retailChnlItemList) throws Exception {
		return retailChnlItemListService.updateById(retailChnlItemList);
	}

	@Override
	public boolean deleteRetailChnlItemListById(String id) throws Exception {
		return retailChnlItemListService.removeById(id);
	}



	@Override
	public PageInfo<RetailChnlItemList> getRetailChnlItemListPage(int startNum, int pageSize,
			RetailChnlItemList retailChnlItemList) throws Exception {
		PageHelper.startPage(startNum, pageSize);
		List<RetailChnlItemList> telCItemList = getRetailChnlItemList(retailChnlItemList);
		for (RetailChnlItemList RetailChnlItemList2 : telCItemList) {
			if (!StringUtil.isNullOrEmpty(RetailChnlItemList2.getOperId()))
				RetailChnlItemList2.setOperId(TelRechargeConstants.OperatorType.findByCode(RetailChnlItemList2.getOperId()));
		}
		PageInfo<RetailChnlItemList> RetailChnlItemListPage = new PageInfo<RetailChnlItemList>(telCItemList);
		return RetailChnlItemListPage;
	}

	@Override
	public int deleteByProductId(String productId) {
		return retailChnlItemListService.deleteByProductId(productId);
	}



	@Override
	public List<RetailChnlItemList> getRetailChnlItemList(RetailChnlItemList retailChnlItemList) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
