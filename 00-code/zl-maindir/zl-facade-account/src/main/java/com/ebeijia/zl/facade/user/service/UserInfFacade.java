package com.ebeijia.zl.facade.user.service;

import com.ebeijia.zl.common.utils.domain.BaseResult;
import com.ebeijia.zl.facade.user.req.OpenUserInfReqVo;
import com.ebeijia.zl.facade.user.vo.PersonInf;
import com.ebeijia.zl.facade.user.vo.UserInf;

/**
 * 
* 
* @ClassName: UserFacade.java
* @Description: 用户信息管理接口
*
* @version: v1.0.0
* @author: zhuqi
* @date: 2018年11月30日 下午1:38:31 
*
* Modification History:
* Date         Author          Version
*-------------------------------------*
* 2018年11月30日     zhuqi           v1.0.0
 */
public interface UserInfFacade {
	
	/**
	 * 
	* @Description: 創建用戶信息
	*
	* @param:描述1描述
	*
	* @version: v1.0.0
	* @author: zhuqi
	* @date: 2018年12月4日 上午10:34:21 
	*
	* Modification History:
	* Date         Author          Version
	*-------------------------------------*
	* 2018年12月4日     zhuqi           v1.0.0
	 */
	BaseResult registerUserInf(OpenUserInfReqVo req);

	/**
	 * 
	* @Function: UserFacade.java
	* @Description: 该函数的功能描述
	*
	* @param:userId 用户Id
	* @return：UserInf 用户对象
	*
	* @version: v1.0.0
	* @author: zhuqi
	* @date: 2018年11月30日 下午1:39:20 
	*
	* Modification History:
	* Date         Author          Version
	*-------------------------------------*
	* 2018年11月30日     zhuqi           v1.0.0
	 */
	UserInf getUserInfByUserId(String userId);
	
	/**
	 * 
	* @Function: UserFacade.java
	* @Description: 外部渠道获取用户信息
	*
	* @param:描述1描述
	* @return：返回结果描述
	* @throws：异常描述
	*
	* @version: v1.0.0
	* @author: zhuqi
	* @date: 2018年11月30日 下午1:40:52 
	*
	* Modification History:
	* Date         Author          Version
	*-------------------------------------*
	* 2018年11月30日     zhuqi           v1.0.0
	 */
	UserInf getUserInfByExternalId(String externalId,String channel);

	/**
	 * 根据手机号查找个人信息 适用于用户注册绑定
	 * @param phoneNo
	 * @return
	 */
	PersonInf getPersonInfByPhoneNo(String phoneNo);

	/**
	 * 查找用户手机号是否在当前渠道注册
	 * @param phoneNo
	 * @param channel
	 * @return
	 */
	UserInf getUserInfByPhoneNo(String phoneNo,String channel);

	/**
	 * 通过手机号查询用户信息
	 * @param phoneNo
	 * @return
	 */
	UserInf getUserInfByMobilePhone(String phoneNo);
}
