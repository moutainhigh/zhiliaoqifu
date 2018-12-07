package com.cn.thinkx.wecard.centre.module.jms.listener;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.alibaba.fastjson.JSONObject;
import com.cn.thinkx.ecom.activemq.core.vo.WechatTemplateParam;
import com.cn.thinkx.ecom.redis.core.constants.RedisConstants;
import com.cn.thinkx.wechat.base.wxapi.process.MpAccount;
import com.cn.thinkx.wechat.base.wxapi.process.WxApiClient;
import com.cn.thinkx.wechat.base.wxapi.vo.TemplateMessage;

import redis.clients.jedis.JedisCluster;

/**
 * 模板消息队列监听器
 * 
 * @author 朱秋友
 * 
 * @since 2017-01-17 11:21:23
 *  
 */
@Configuration
public class TemplateSessionAwareMessageListener implements MessageListener {
	private Logger logger = LoggerFactory.getLogger(TemplateSessionAwareMessageListener.class);

	@Autowired
	private JedisCluster jedisCluster;
	
	@Autowired
	private WxApiClient wxApiClient;

	public synchronized void onMessage(Message message) {
		try {
			ActiveMQTextMessage msg = (ActiveMQTextMessage) message;
			final String ms = msg.getText();
			logger.info("待发送的模板消息{}", ms);
			WechatTemplateParam templatePatam = com.alibaba.fastjson.JSONObject.parseObject(ms, WechatTemplateParam.class);
			MpAccount mpAccount = wxApiClient.getMpAccount(templatePatam.getAcountName());

			TemplateMessage tplMsg = new TemplateMessage();
			tplMsg.setTouser(templatePatam.getTouser());
			tplMsg.setTemplate_id(jedisCluster.hget(RedisConstants.REDIS_HASH_TABLE_TB_BASE_DICT_KV, templatePatam.getTemplate_id()));
			tplMsg.setUrl(templatePatam.getUrl());
			tplMsg.setData(templatePatam.getData());
			JSONObject result = wxApiClient.sendTemplateMessage(tplMsg, mpAccount);
			logger.info("发送模板消息队列状态{}", result.toString());
			if (result != null && result.getIntValue("errcode") == 0) 
				message.acknowledge();
			
		} catch (Exception e) {
			logger.error("## 待发送的模板消息异常：", e);
		}
	}
}