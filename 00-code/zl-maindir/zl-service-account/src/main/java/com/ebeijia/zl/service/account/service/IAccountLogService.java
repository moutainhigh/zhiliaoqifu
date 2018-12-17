package com.ebeijia.zl.service.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ebeijia.zl.facade.account.dto.AccountInf;
import com.ebeijia.zl.facade.account.dto.AccountLog;
import com.ebeijia.zl.facade.account.dto.TransLog;
import com.ebeijia.zl.facade.account.exceptions.AccountBizException;
import com.ebeijia.zl.facade.account.req.AccountQueryReqVo;
import com.ebeijia.zl.facade.account.vo.AccountLogVo;

import java.util.List;


/**
 *
 * 账户交易日志 Service 接口类
 *
 * @User zhuqi
 * @Date 2018-11-30
 */
public interface IAccountLogService extends IService<AccountLog> {

	boolean save(AccountInf accountInf,TransLog transLog)throws AccountBizException;

	/**
	 * 账单查询
	 * @param req
	 * @return
	 */
	List<AccountLogVo> getAccountLogVoList(AccountQueryReqVo req);
}
