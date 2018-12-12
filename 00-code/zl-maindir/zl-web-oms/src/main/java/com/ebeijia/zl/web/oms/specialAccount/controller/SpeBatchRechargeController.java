package com.ebeijia.zl.web.oms.specialAccount.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.ebeijia.zl.basics.billingtype.domain.BillingTypeInf;
import com.ebeijia.zl.basics.billingtype.service.BillingTypeInfService;
import com.ebeijia.zl.common.utils.constants.Constants;
import com.ebeijia.zl.common.utils.enums.SpecAccountTypeEnum;
import com.ebeijia.zl.common.utils.enums.TransCode;
import com.ebeijia.zl.common.utils.enums.UserType;
import com.ebeijia.zl.common.utils.tools.NumberUtils;
import com.ebeijia.zl.common.utils.tools.StringUtil;
import com.ebeijia.zl.core.redis.utils.JedisClusterUtils;
import com.ebeijia.zl.facade.telrecharge.domain.CompanyInf;
import com.ebeijia.zl.facade.telrecharge.service.CompanyInfFacade;
import com.ebeijia.zl.web.oms.common.util.OmsEnum.BatchOrderStat;
import com.ebeijia.zl.web.oms.specialAccount.model.SpeAccountBatchOrder;
import com.ebeijia.zl.web.oms.specialAccount.model.SpeAccountBatchOrderList;
import com.ebeijia.zl.web.oms.specialAccount.service.SpeAccountBatchOrderListService;
import com.ebeijia.zl.web.oms.specialAccount.service.SpeAccountBatchOrderService;
import com.ebeijia.zl.web.oms.specialAccount.util.OrderConstants;
import com.ebeijia.zl.web.oms.specialAccount.util.PagePersonUtil;
import com.ebeijia.zl.web.oms.sys.model.User;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping(value = "speaccount/batchRecharge")
public class SpeBatchRechargeController {

	Logger logger = LoggerFactory.getLogger(SpeBatchRechargeController.class);

	@Autowired
	private SpeAccountBatchOrderService speAccountBatchOrderService;
	
	@Autowired
	private SpeAccountBatchOrderListService speAccountBatchOrderListService;
	
	@Autowired
	private BillingTypeInfService billingTypeInfService;
	
	@Autowired
	@Qualifier("jedisClusterUtils")
	private JedisClusterUtils jedisClusterUtils;
	
	@Autowired
	@Qualifier("companyInfFacade")
	private CompanyInfFacade companyInfFacade;

	/**
	 * 批量充值列表
	 * 
	 * @param req
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/listRecharge")
	public ModelAndView listRecharge(HttpServletRequest req, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("specialAccount/batchRecharge/listRecharge");
		String operStatus = StringUtil.nullToString(req.getParameter("operStatus"));
		PageInfo<SpeAccountBatchOrder> pageList = null;
		int startNum = NumberUtils.parseInt(req.getParameter("pageNum"), 1);
		int pageSize = NumberUtils.parseInt(req.getParameter("pageSize"), 10);		
		SpeAccountBatchOrder order = new SpeAccountBatchOrder();
		order.setOrderType(TransCode.MB50.getCode());
		try {
			pageList = speAccountBatchOrderService.getSpeAccountBatchOrderPage(startNum, pageSize, order, req);
		} catch (Exception e) {
			logger.error("## 查询批量充值列表信息出错", e);
		}
		List<CompanyInf> companyList = companyInfFacade.getCompanyInfList(new CompanyInf());
		mv.addObject("order", order);
		mv.addObject("mapOrderStat", BatchOrderStat.values());
		mv.addObject("pageInfo", pageList);
		mv.addObject("operStatus", operStatus);
//		mv.addObject("accountTypeList", UserType.TYPE100.getValue());
		mv.addObject("companyList", companyList);
		return mv;
	}

	/**
	 * 新增批量充值
	 * 
	 * @param req
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/intoAddRecharge")
	public ModelAndView intoAddRecharge(HttpServletRequest req, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("specialAccount/batchRecharge/addRecharge");
		BigDecimal sumMoney = BigDecimal.ZERO;
		List<CompanyInf> companyList = companyInfFacade.getCompanyInfList(new CompanyInf());
		List<BillingTypeInf> billingTypeList = billingTypeInfService.getBillingTypeInfList(new BillingTypeInf());
		
		LinkedList<SpeAccountBatchOrderList> orderList = speAccountBatchOrderListService.getRedisBatchOrderList(OrderConstants.speBathRechargeSession);
//		LinkedList<SpeAccountBatchOrderList> orderList = PagePersonUtil.getRedisBatchOrderList(OrderConstants.speBathRechargeSession);
		int startNum = NumberUtils.parseInt(req.getParameter("pageNum"), 1);
		int pageSize = NumberUtils.parseInt(req.getParameter("pageSize"), 10);	
		Page<SpeAccountBatchOrderList> page = new Page<>(startNum, pageSize, false);
		if (orderList != null && orderList.size() >= 1) {
			double sum = orderList.stream().mapToDouble(o -> Double.valueOf(o.getAmount())).summaryStatistics().getSum();
			sumMoney = sumMoney.add(BigDecimal.valueOf(sum));
			page.setTotal(orderList.size());
			List<SpeAccountBatchOrderList> list = PagePersonUtil.getPersonInfPageList(startNum, pageSize, orderList);
			list.forEach(o ->{
				page.add(o);
			});
		} else {
			page.setTotal(0);
		}
		PageInfo<SpeAccountBatchOrderList> pageList = new PageInfo<SpeAccountBatchOrderList>(page);
		mv.addObject("pageInfo", pageList);
		mv.addObject("count", pageList.getTotal());
		mv.addObject("sumMoney", sumMoney.doubleValue());
		mv.addObject("accountType", UserType.TYPE100);
		mv.addObject("companyList", companyList);
		mv.addObject("billingTypeList", billingTypeList);
		return mv;
	}

	/**
	 * 批量充值提交
	 * 
	 * @param req
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/addRechargeCommit")
	@ResponseBody
	public ModelMap addRechargeCommit(HttpServletRequest req, HttpServletResponse response) {
		ModelMap resultMap = new ModelMap();
		resultMap.addAttribute("status", Boolean.TRUE);
		int i = 0;
		LinkedList<SpeAccountBatchOrderList> orderList = speAccountBatchOrderListService.getRedisBatchOrderList(OrderConstants.speBathRechargeSession);
		if (orderList == null || orderList.size() < 1) {
			resultMap.addAttribute("status", Boolean.FALSE);
			resultMap.addAttribute("msg", "没有添加任何数据！！！");
			return resultMap;
		}
		try {
			i = speAccountBatchOrderService.addSpeAccountBatchOrder(req, orderList, TransCode.MB50.getCode());
			if (i > 0) {
				jedisClusterUtils.del(OrderConstants.speBathRechargeSession);
			}
		} catch (Exception e) {
			logger.error("## 新增批量充值订单出错----->>[{}]", e.getMessage());
			resultMap.addAttribute("status", Boolean.FALSE);
			resultMap.addAttribute("msg", "新增批量充值失败，请重新添加");
		}
		return resultMap;
	}

	/**
	 * 批量充值订单详情
	 * 
	 * @param req
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/intoViewRecharge")
	public ModelAndView intoViewRecharge(HttpServletRequest req, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("specialAccount/batchRecharge/viewRecharge");
		String orderId = req.getParameter("orderId");
		SpeAccountBatchOrder order = speAccountBatchOrderService.getSpeAccountBatchOrderByOrderId(orderId);
		order.setOrderStat(BatchOrderStat.findStat(order.getOrderStat()));
		int startNum = NumberUtils.parseInt(req.getParameter("pageNum"), 1);
		int pageSize = NumberUtils.parseInt(req.getParameter("pageSize"), 10);
		PageInfo<SpeAccountBatchOrderList> pageList = speAccountBatchOrderListService.getSpeAccountBatchOrderListPage(startNum, pageSize, orderId);
		mv.addObject("order", order);
		mv.addObject("pageInfo", pageList);
		return mv;
	}

	/**
	 * 进入批量充值订单编辑
	 * 
	 * @param req
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/intoEditRecharge")
	public ModelAndView intoEditRecharge(HttpServletRequest req, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("specialAccount/batchRecharge/editRecharge");
		String orderId = req.getParameter("orderId");
		String operStatus = StringUtil.nullToString(req.getParameter("operStatus"));
		SpeAccountBatchOrder order = speAccountBatchOrderService.getSpeAccountBatchOrderByOrderId(orderId);
		order.setOrderStat(BatchOrderStat.findStat(order.getOrderStat()));
		int startNum = NumberUtils.parseInt(req.getParameter("pageNum"), 1);
		int pageSize = NumberUtils.parseInt(req.getParameter("pageSize"), 10);
		PageInfo<SpeAccountBatchOrderList> pageList = speAccountBatchOrderListService.getSpeAccountBatchOrderListPage(startNum, pageSize, orderId);
		List<SpeAccountBatchOrderList> list = new ArrayList<>();
		list = speAccountBatchOrderListService.getSpeAccountBatchOrderListByOrderId(orderId);
//		list = list.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(SpeAccountBatchOrderList::getBizType))), ArrayList::new));
		mv.addObject("order", order);
		mv.addObject("pageInfo", pageList);
		mv.addObject("operStatus", operStatus);
		mv.addObject("bizType", list.get(0).getBizType());
		mv.addObject("bizTypeName", SpecAccountTypeEnum.findByBId(list.get(0).getBizType()).getName());
		mv.addObject("accountType", list.get(0).getAccountType());
		mv.addObject("accountTypeName", UserType.findByCode(list.get(0).getAccountType()).getValue());
		return mv;
	}

	/**
	 * 删除批量充值订单
	 * 
	 * @param req
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/deleteRechargeCommit")
	@ResponseBody
	public ModelMap deleteRechargeCommit(HttpServletRequest req, HttpServletResponse response) {
		ModelMap resultMap = new ModelMap();
		resultMap.put("status", Boolean.TRUE);
		String orderId = StringUtil.nullToString(req.getParameter("orderId"));
		HttpSession session = req.getSession();
		User user = (User)session.getAttribute(Constants.SESSION_USER);
		try {
			int i = speAccountBatchOrderService.deleteOpenAccountCommit(orderId, user);
			if (i == 0) {
				resultMap.put("status", Boolean.FALSE);
				resultMap.put("msg", "删除批量充值订单失败");
			}
		} catch (Exception e) {
			resultMap.put("status", Boolean.FALSE);
			logger.error("## 删除批量充值订单出错,订单号：[{}]", orderId, e);
		}
		return resultMap;
	}

	/**
	 * 提交批量充值订单
	 * 
	 * @param req
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/addOrderCommit")
	@ResponseBody
	public ModelMap addOrderCommit(HttpServletRequest req, HttpServletResponse response) {
		ModelMap resultMap = new ModelMap();
		resultMap.put("status", Boolean.TRUE);
		String orderId = StringUtil.nullToString(req.getParameter("orderId"));
		HttpSession session = req.getSession();
		User user = (User)session.getAttribute(Constants.SESSION_USER);
		try {
			SpeAccountBatchOrder order = speAccountBatchOrderService.getSpeAccountBatchOrderById(orderId);
			if (BatchOrderStat.BatchOrderStat_10.getCode().equals(order.getOrderStat())) {
				int i = speAccountBatchOrderService.batchSpeAccountRechargeITF(orderId, user, BatchOrderStat.BatchOrderStat_10.getCode());
				if (i == 0) {
					resultMap.put("status", Boolean.FALSE);
					resultMap.put("msg", "提交批量开户订单失败");
					return resultMap;
				}
			}
		} catch (Exception e) {
			resultMap.put("status", Boolean.FALSE);
			logger.error("## 提交批量充值订单，订单号：[{}]，出错", orderId, e);
		}
		return resultMap;
	}

	/**
	 * 重复提交批量充值订单
	 * 
	 * @param req
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/addOrderAgainCommit")
	@ResponseBody
	public ModelMap addOrderAgainCommit(HttpServletRequest req, HttpServletResponse response) {
		ModelMap resultMap = new ModelMap();
		resultMap.put("status", Boolean.TRUE);
		String orderId = StringUtil.nullToString(req.getParameter("orderId"));
		HttpSession session = req.getSession();
		User user = (User)session.getAttribute(Constants.SESSION_USER);
		try {
			SpeAccountBatchOrder order = speAccountBatchOrderService.getSpeAccountBatchOrderById(orderId);
			if (BatchOrderStat.BatchOrderStat_99.getCode().equals(order.getOrderStat())) {
				speAccountBatchOrderService.batchSpeAccountRechargeITF(orderId, user, BatchOrderStat.BatchOrderStat_99.getCode());
			}
		} catch (Exception e) {
			resultMap.put("status", Boolean.FALSE);
			logger.error("## 重复提交批量充值订单，订单号：[{}],出错[{}]", orderId, e);
		}
		return resultMap;
	}

	/**
	 * 批量充值添加充值用户信息
	 * 
	 * @param req
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/addOrderListCommit")
	@ResponseBody
	public ModelMap addOrderListCommit(HttpServletRequest req, HttpServletResponse response) {
		ModelMap resultMap = new ModelMap();
		resultMap.put("status", Boolean.TRUE);
		String orderId = StringUtil.nullToString(req.getParameter("orderId"));
		String bizType = StringUtil.nullToString(req.getParameter("bizType")); 
		HttpSession session = req.getSession();
		User user = (User)session.getAttribute(Constants.SESSION_USER);
		try {
			SpeAccountBatchOrderList orderList = new SpeAccountBatchOrderList();
			orderList.setOrderId(orderId);
			orderList.setUserName(StringUtil.nullToString(req.getParameter("name")));
			orderList.setPhoneNo(StringUtil.nullToString(req.getParameter("phone")));
			orderList.setUserCardNo(StringUtil.nullToString(req.getParameter("card")));
			orderList.setAccountType(StringUtil.nullToString(req.getParameter("accountType")));
			orderList.setAmount(Double.valueOf(NumberUtils.RMBYuanToCent(StringUtil.nullToString(req.getParameter("money")))));
			orderList.setCreateUser(user.getId().toString());
			orderList.setUpdateUser(user.getId().toString());
			orderList.setCreateTime(System.currentTimeMillis());
			orderList.setUpdateTime(System.currentTimeMillis());
			
			String[] bizTypes = {bizType};
			
			List<SpeAccountBatchOrderList> orderList2 = speAccountBatchOrderListService.getSpeAccountBatchOrderListByOrder(orderList);
			if (orderList2 != null && orderList2.size() > 0) {
				resultMap.put("status", Boolean.FALSE);
				resultMap.put("msg", "电话号码重复！！！");
				return resultMap;
			}
			speAccountBatchOrderListService.addOrderList(orderList, user, bizTypes);
		} catch (Exception e) {
			resultMap.put("status", Boolean.FALSE);
			resultMap.put("msg", "系统故障，请稍后再试");
			logger.error("## 批量充值添加充值信息出错", e);
		}
		return resultMap;
	}

	/**
	 * 删除批量充值的订单信息
	 * 
	 * @param req
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/deleteOrderListCommit")
	@ResponseBody
	public ModelMap deleteOrderListCommit(HttpServletRequest req, HttpServletResponse response) {
		ModelMap resultMap = new ModelMap();
		resultMap.put("status", Boolean.TRUE);
		try {
			speAccountBatchOrderListService.deleteSpeAccountBatchOrderList(req);
		} catch (Exception e) {
			resultMap.put("status", Boolean.FALSE);
			resultMap.put("msg", "系统故障，请稍后再试");
			logger.error("## 删除批量充值信息出错", e);
		}
		return resultMap;
	}

	/**
	 * 添加充值用户信息
	 * 
	 * @param req
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/addAccountInf")
	@ResponseBody
	public ModelMap addAccountInf(HttpServletRequest req, HttpServletResponse response) {
		ModelMap resultMap = new ModelMap();
		resultMap.put("status", Boolean.TRUE);
		try {
			String phone = StringUtil.nullToString(req.getParameter("phone"));
			String money = StringUtil.nullToString(req.getParameter("money"));
			SpeAccountBatchOrderList order = new SpeAccountBatchOrderList();
			order.setPuId(UUID.randomUUID().toString().replace("-", ""));
			order.setUserName(StringUtil.nullToString(req.getParameter("name")));
			order.setUserCardNo(StringUtil.nullToString(req.getParameter("card")));
			order.setPhoneNo(phone);
			order.setAmount(Double.valueOf(NumberUtils.RMBCentToYuan(NumberUtils.RMBYuanToCent(money))));
			
			LinkedList<SpeAccountBatchOrderList> orderList = speAccountBatchOrderListService.getRedisBatchOrderList(OrderConstants.speBathRechargeSession);
//			LinkedList<SpeAccountBatchOrderList> orderList = PagePersonUtil.getRedisBatchOrderList(OrderConstants.speBathRechargeSession);
			if (orderList != null && orderList.size() >= 1) {
				orderList.forEach(o ->{
					if (o.getPhoneNo().equals(phone)) {
						resultMap.put("status", Boolean.FALSE);
						resultMap.put("msg", "电话号码重复！！！");
						return;
					}
				});
			} else {
				orderList = new LinkedList<>();
			}
			orderList.addFirst(order);
			jedisClusterUtils.setex(OrderConstants.speBathRechargeSession, JSON.toJSON(orderList).toString(), 1800); // 设置有效时间30分钟
		} catch (Exception e) {
			resultMap.put("status", Boolean.FALSE);
			resultMap.put("msg", "系统故障，请稍后再试");
			logger.error("## 批量充值新增，添加充值名单出错！", e);
		}
		return resultMap;
	}

	/**
	 * 删除批量充值用户信息
	 * 
	 * @param req
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/deleteAccountInf")
	@ResponseBody
	public ModelMap deleteAccountInf(HttpServletRequest req, HttpServletResponse response) {
		ModelMap resultMap = new ModelMap();
		resultMap.put("status", Boolean.TRUE);
		try {
			String puId = req.getParameter("puId");
			LinkedList<SpeAccountBatchOrderList> orderList = speAccountBatchOrderListService.getRedisBatchOrderList(OrderConstants.speBathRechargeSession);
			for (SpeAccountBatchOrderList o : orderList) {
				if (o.getPuId().equals(puId)) {
					orderList.remove(o);
				}
			}
			jedisClusterUtils.setex(OrderConstants.speBathRechargeSession, JSON.toJSON(orderList).toString(), 1800); // 设置有效时间30分钟
		} catch (Exception e) {
			resultMap.put("status", Boolean.FALSE);
			resultMap.put("msg", "系统故障，请稍后再试");
			logger.error("## 批量充值新增，删除充值用户名单出错", e);
		}
		return resultMap;
	}

}