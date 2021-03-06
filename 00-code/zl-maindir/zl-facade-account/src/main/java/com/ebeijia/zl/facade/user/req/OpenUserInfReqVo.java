package com.ebeijia.zl.facade.user.req;

import com.ebeijia.zl.common.utils.req.BaseTxnReq;

/**
* @Description: 账户用户信息请求
*
* @version: v1.0.0
* @author: zhuqi
* @date: 2018年12月3日 下午7:45:37 
*
* Modification History:
* Date         Author          Version
*-------------------------------------*
* 2018年12月3日     zhuqi           v1.0.0
 */
public class OpenUserInfReqVo extends BaseTxnReq {

	/**
	 * 用戶名稱
	 */
	private String userName;
	
	
	/**
	 * 手機號
	 */
	private String mobilePhone;
	
	/**
	 * 证件类型
	 */
	private String cardType;
	
	/**
	 * 证件编号
	 */
	private String icardNo;
	
	
	/**
	 * 所属企業
	 */
	private String companyId;
	



	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getMobilePhone() {
		return mobilePhone;
	}


	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}


	public String getCardType() {
		return cardType;
	}


	public void setCardType(String cardType) {
		this.cardType = cardType;
	}


	public String getIcardNo() {
		return icardNo;
	}


	public void setIcardNo(String icardNo) {
		this.icardNo = icardNo;
	}


	public String getCompanyId() {
		return companyId;
	}


	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}


}
