/*package com.cn.thinkx.oms.specialAccount.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cn.thinkx.oms.specialAccount.model.SpeAccountBatchOrderList;
import com.github.pagehelper.PageInfo;

public interface SpeAccountBatchOrderListService {

	*//**
	 * 订单明细列表
	 * @param orderId
	 * @return
	 *//*
	List<SpeAccountBatchOrderList> getSpeAccountBatchOrderListList(String orderId);
	
	*//**
	 * 批量添加订单明细
	 * @param list
	 * @return
	 *//*
	int addSpeAccountBatchOrderList(List<SpeAccountBatchOrderList> list);
	
	*//**
	 * 订单明细列表 (带分页)
	 * @param startNum
	 * @param pageSize
	 * @param orderId
	 * @return
	 *//*
	PageInfo<SpeAccountBatchOrderList> getSpeAccountBatchOrderListPage(int startNum, int pageSize, String orderId);
	
	*//**
	 * 添加订单明细
	 * @param orderList
	 * @return
	 *//*
	int addOrderList(SpeAccountBatchOrderList orderList);
	
	*//**
	 * 删除订单明细
	 * @param req
	 * @return
	 *//*
	int deleteSpeAccountBatchOrderList(HttpServletRequest req);
	
	*//**
	 * 修改订单明细
	 * @param orderList
	 * @return
	 *//*
	int updateSpeAccountBatchOrderList(SpeAccountBatchOrderList orderList);
	
	*//**
	 * 查询处理失败订单明细
	 * @param orderId
	 * @return
	 *//*
	List<SpeAccountBatchOrderList> getSpeAccountBatchOrderListFailList(String orderId);
	
	*//**
	 * 根据手机号码询订单明细
	 * @param orderList
	 * @return
	 *//*
	SpeAccountBatchOrderList getSpeAccountBatchOrderListByOrderIdAndPhoneNo(SpeAccountBatchOrderList orderList);
	
	*//**
	 * 根据主键查询订单明细
	 * @param orderListId
	 * @return
	 *//*
	SpeAccountBatchOrderList getSpeAccountBatchOrderListByOrderListId(String orderListId);
}
*/