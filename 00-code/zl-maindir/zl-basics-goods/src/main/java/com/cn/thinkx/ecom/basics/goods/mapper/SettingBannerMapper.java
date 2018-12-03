package com.cn.thinkx.ecom.basics.goods.mapper;

import com.cn.thinkx.ecom.basics.goods.domain.SettingBanner;
import com.ebeijia.zl.common.core.mapper.BaseDao;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SettingBannerMapper extends BaseDao<SettingBanner> {

	/**
	 * 查询商城服务对应的banner
	 * 
	 * @param entity
	 * @return
	 */
	List<SettingBanner> getSettingBannerList(SettingBanner entity);
	
	/**
	 * 查询商城服务没有的banner信息
	 * 
	 * @param entity
	 * @return
	 */
	List<SettingBanner> getNotSettingBannerList(SettingBanner entity);
	
	/**
	 * 刪除商城服务没有的banner信息
	 * 
	 * @param entity
	 * @return
	 */
	int deleteBySettingIdAndBannerId(SettingBanner entity);
}
