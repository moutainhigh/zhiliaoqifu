package com.ebeijia.zl.web.user.model.customer.ctrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ebeijia.zl.web.user.model.utils.JsonView;
import com.ebeijia.zl.web.user.model.utils.UploadUtil;
import com.ebeijia.zl.web.user.model.wxapi.service.BizService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.ebeijia.zl.basics.wechat.domain.AccountFans;
import com.ebeijia.zl.common.utils.tools.StringUtil;
import com.ebeijia.zl.core.redis.utils.RedisDictProperties;
import com.ebeijia.zl.core.wechat.aes.WXBizMsgCrypt;
import com.ebeijia.zl.core.wechat.process.MpAccount;
import com.ebeijia.zl.core.wechat.process.MsgXmlUtil;
import com.ebeijia.zl.core.wechat.process.WxApiClient;
import com.ebeijia.zl.core.wechat.process.WxMemoryCacheClient;
import com.ebeijia.zl.core.wechat.process.WxSign;
import com.ebeijia.zl.core.wechat.util.WxSignUtil;
import com.ebeijia.zl.core.wechat.vo.MsgRequest;
import com.ebeijia.zl.core.wechat.vo.SemaphoreMap;


/**
 * 微信与开发者服务器交互接口
 */
@RestController
@RequestMapping("/wchat/wxapi")
public class WxApiCtrl {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BizService bizService;
	
	@Autowired
	private WxMemoryCacheClient wxMemoryCacheClient;
	
	@Autowired
	private RedisDictProperties redisDictProperties;
	
	@Autowired
	private WxApiClient  wxApiClient;

	/**
	 * GET请求：进行URL、Tocken 认证； 1. 将token、timestamp、nonce三个参数进行字典序排序 2.
	 * 将三个参数字符串拼接成一个字符串进行sha1加密 3. 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
	 */
	@RequestMapping(value = "/{account}/message", method = RequestMethod.GET)
	public @ResponseBody String doGet(HttpServletRequest request, @PathVariable String account) {
		// 如果是多账号，根据url中的account参数获取对应的MpAccount处理即可
		MpAccount mpAccount = wxMemoryCacheClient.getSingleMpAccount();// 获取缓存中的唯一账号
		if (mpAccount != null) {
			String token = mpAccount.getToken();// 获取token，进行验证；
			String signature = request.getParameter("signature");// 微信加密签名
			String timestamp = request.getParameter("timestamp");// 时间戳
			String nonce = request.getParameter("nonce");// 随机数
			String echostr = request.getParameter("echostr");// 随机字符串

			// 校验成功返回 echostr，成功成为开发者；否则返回error，接入失败
			if (WxSignUtil.validSign(signature, token, timestamp, nonce)) {
				return echostr;
			}
		}
		return "error";
	}

	/**
	 * POST 请求：进行消息处理(核心业务controller)
	 */
	@RequestMapping(value = "/{account}/message", method = RequestMethod.POST)
	public @ResponseBody String doPost(HttpServletRequest request, HttpServletResponse response, @PathVariable String account) {
		try {
			MpAccount mpAccount = wxMemoryCacheClient.getSingleMpAccount();
			String wxAESKey = redisDictProperties.getdictValueByCode("WX_AES_KEY");// 加密密钥
			String token = mpAccount.getToken();// token
			String appId = mpAccount.getAppid();
			String signature = request.getParameter("msg_signature");// 微信加密签名
			String timestamp = request.getParameter("timestamp");// 时间戳
			String nonce = request.getParameter("nonce");// 随机数

			WXBizMsgCrypt pc = new WXBizMsgCrypt(token, wxAESKey, appId);
			MsgRequest msgRequest = MsgXmlUtil.parseXml(request, pc, wxAESKey, appId, token, signature, timestamp, nonce);// 获取发送的消息
			String msgId = msgRequest.getMsgId();
			String createTimeAndFromUserName = msgRequest.getCreateTime() + msgRequest.getFromUserName();
			if (!StringUtil.isNullOrEmpty(msgId)) {
				if (SemaphoreMap.getSemaphore().containsKey(msgId)) {
					logger.info("消息重复推送msgId [{}]--->FromUserName [{}], MsgType [{}], Event [{}]", msgId, 
							msgRequest.getFromUserName(), msgRequest.getMsgType(), msgRequest.getEvent());
					return "success";
				} else {
					SemaphoreMap.getSemaphore().put(msgId, msgId);
				}
			} else if (!StringUtil.isNullOrEmpty(createTimeAndFromUserName)) {
				if (SemaphoreMap.getSemaphore().containsKey(createTimeAndFromUserName)) {
					logger.info("消息重复推送createTimeAndFromUserName [{}]--->FromUserName [{}], MsgType [{}], Event [{}]", 
							createTimeAndFromUserName, msgRequest.getFromUserName(), msgRequest.getMsgType(), msgRequest.getEvent());
					return "success";
				} else {
					SemaphoreMap.getSemaphore().put(createTimeAndFromUserName, createTimeAndFromUserName);
				}
			} else {
				logger.info("消息重复推送msgId [{}]--->FromUserName [{}], MsgType [{}], Event [{}]", msgId, 
						msgRequest.getFromUserName(), msgRequest.getMsgType(), msgRequest.getEvent());
				return "success";
			}

			String rtnXml = bizService.processMsg(msgRequest, mpAccount);// 处理完业务逻辑后回复微信平台
//			logger.info("消息被动回复推送rtnXml [{}]--->FromUserName [{}], MsgType [{}], Event [{}]", rtnXml, 
//					msgRequest.getFromUserName(), msgRequest.getMsgType(), msgRequest.getEvent());
			
			if (StringUtil.isNullOrEmpty(rtnXml) || "null".equals(rtnXml)) {
				return "success";
			} else if ("success".equals(rtnXml) || "error".equals(rtnXml)) {
				return rtnXml;
			} else {
				String encryptXml = pc.encryptMsg(rtnXml, timestamp, nonce);// 返回消息加密
				return encryptXml;
			}
		} catch (Exception e) {
			logger.error("## 公众号消息处理发生异常：", e);
			return "error";
		}
	}


	// 获取用户列表
	@RequestMapping(value = "/syncAccountFansList")
	public ModelAndView syncAccountFansList() {
		MpAccount mpAccount = wxMemoryCacheClient.getSingleMpAccount();// 获取缓存中的唯一账号
		if (mpAccount != null) {
			boolean flag = bizService.syncAccountFansList(mpAccount);
			if (flag) {
				return new ModelAndView("redirect:/accountfans/paginationEntity.html");
			}
		}
		ModelAndView mv = new ModelAndView("common/failure");
		mv.addObject("failureMsg", "获取用户列表失败");
		return mv;
	}

	// 根据用户的ID更新用户信息
	@RequestMapping(value = "/syncAccountFans")
	public ModelAndView syncAccountFans(String openId) {
		ModelAndView mv = new ModelAndView("common/failure");
		MpAccount mpAccount = wxMemoryCacheClient.getSingleMpAccount();// 获取缓存中的唯一账号
		if (mpAccount != null) {
			AccountFans fans = bizService.syncAccountFans(openId, mpAccount, true);// 同时更新数据库
			if (fans != null) {
				mv.setViewName("wxcms/fansInfo");
				mv.addObject("fans", fans);
				mv.addObject("cur_nav", "fans");
				return mv;
			}
		}
		mv.addObject("failureMsg", "获取用户信息失败,公众号信息或openid信息错误");
		return mv;
	}



	// 获取openid
	@RequestMapping(value = "/oauthOpenid")
	public ModelAndView oauthOpenid(HttpServletRequest request) {
		MpAccount mpAccount = wxMemoryCacheClient.getSingleMpAccount();// 获取缓存中的唯一账号
		if (mpAccount != null) {
			ModelAndView mv = new ModelAndView("wxweb/oauthOpenid");
			// 拦截器已经处理了缓存,这里直接取
			String openid = wxMemoryCacheClient.getOpenid(request);
			AccountFans fans = bizService.syncAccountFans(openid, mpAccount, false);// 同时更新数据库
			mv.addObject("openid", openid);
			mv.addObject("fans", fans);
			return mv;
		} else {
			ModelAndView mv = new ModelAndView("common/failureMobile");
			mv.addObject("message", "OAuth获取openid失败");
			return mv;
		}
	}

	/**
	 * 生成二维码
	 * 
	 * @param request
	 * @param num
	 *            二维码参数
	 * @return
	 */
	@RequestMapping(value = "/createQrcode", method = RequestMethod.POST)
	public ModelAndView createQrcode(HttpServletRequest request, Integer num) {
		ModelAndView mv = new ModelAndView("wxcms/qrcode");
		mv.addObject("cur_nav", "qrcode");
		MpAccount mpAccount = wxMemoryCacheClient.getSingleMpAccount();// 获取缓存中的唯一账号
		if (num != null) {
			byte[] qrcode = wxApiClient.createQRCode(180, num, mpAccount);// 有效期180s
			String url =UploadUtil.byteToImg(request.getServletContext().getRealPath("/"), qrcode);
			mv.addObject("qrcode", url);
		}
		mv.addObject("num", num);
		return mv;
	}

	// 以根据openid群发文本消息为例
	@RequestMapping(value = "/massSendTextMsg", method = RequestMethod.POST)
	public void massSendTextMsg(HttpServletResponse response, String openid, String content) {
		content = "群发文本消息";
		MpAccount mpAccount = wxMemoryCacheClient.getSingleMpAccount();// 获取缓存中的唯一账号
		String rstMsg = "根据openid群发文本消息失败";
		if (mpAccount != null && !StringUtils.isBlank(openid)) {
			List<String> openidList = new ArrayList<String>();
			openidList.add(openid);
			// 根据openid群发文本消息
			JSONObject result = wxApiClient.massSendTextByOpenIds(openidList, content, mpAccount);

			try {
				if (result.getInteger("errcode") != 0) {
					response.getWriter().write("send failure");
				} else {
					response.getWriter().write("send success");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ModelAndView mv = new ModelAndView("common/failure");
		mv.addObject("failureMsg", rstMsg);
	}

	/**
	 * 发送客服消息
	 * 
	 * @param openId
	 *            ： 粉丝的openid
	 * @param content
	 *            ： 消息内容
	 * @return
	 */
	@RequestMapping(value = "/sendCustomTextMsg", method = RequestMethod.POST)
	public void sendCustomTextMsg(HttpServletRequest request, HttpServletResponse response, String openid) {
		MpAccount mpAccount = wxMemoryCacheClient.getSingleMpAccount();// 获取缓存中的唯一账号
		String content = "测试客服消息";
		JSONObject result = wxApiClient.sendCustomTextMessage(openid, content, mpAccount);
		try {
			if (result.getInteger("errcode") != 0) {
				response.getWriter().write("send failure");
			} else {
				response.getWriter().write("send success");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取js ticket
	 * 
	 * @param request
	 * @param url
	 * @return
	 */
	@RequestMapping(value = "/jsTicket")
	@ResponseBody
	public JsonView jsTicket(HttpServletRequest request, String url) {
		MpAccount mpAccount = wxMemoryCacheClient.getSingleMpAccount();// 获取缓存中的唯一账号
		String jsTicket = wxApiClient.getJSTicket(mpAccount);
		WxSign sign = new WxSign(mpAccount.getAppid(), jsTicket, url);
		JsonView jv = new JsonView();
		jv.setData(sign);
		return jv;
	}

}
