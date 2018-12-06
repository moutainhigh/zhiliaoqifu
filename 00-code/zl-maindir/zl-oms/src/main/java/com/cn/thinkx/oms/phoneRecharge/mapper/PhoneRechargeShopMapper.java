package com.cn.thinkx.oms.phoneRecharge.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.cn.thinkx.oms.phoneRecharge.model.PhoneRechargeShop;

@Mapper
public interface PhoneRechargeShopMapper {

	List<PhoneRechargeShop> getPhoneRechargeShopList(PhoneRechargeShop pps);
	
	int insertPhoneRechargeShop(PhoneRechargeShop pps);
	
	int updatePhoneRechargeShop(PhoneRechargeShop pps);
	
	int deletePhoneRechargeShop(String id);
	
	PhoneRechargeShop getPhoneRechargeShopById(String id);
	
	PhoneRechargeShop getPhoneRechargeShopByPhoneRechargeShop(PhoneRechargeShop pps);
	
	List<PhoneRechargeShop> getShopFaceByPhoneRechargeShop(PhoneRechargeShop pps);
	
	List<PhoneRechargeShop> getYDShopFaceByPhoneRechargeShop(PhoneRechargeShop pps);
	
	List<PhoneRechargeShop> getLTShopFaceByPhoneRechargeShop(PhoneRechargeShop pps);
	
	List<PhoneRechargeShop> getDXShopFaceByPhoneRechargeShop(PhoneRechargeShop pps);
}
