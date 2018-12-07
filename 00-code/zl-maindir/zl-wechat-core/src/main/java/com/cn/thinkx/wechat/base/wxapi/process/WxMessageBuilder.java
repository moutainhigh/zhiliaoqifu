package com.cn.thinkx.wechat.base.wxapi.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.cn.thinkx.wechat.base.wxapi.domain.MsgNews;
import com.cn.thinkx.wechat.base.wxapi.domain.MsgText;
import com.cn.thinkx.wechat.base.wxapi.vo.Article;
import com.cn.thinkx.wechat.base.wxapi.vo.ArticleInfo;
import com.cn.thinkx.wechat.base.wxapi.vo.CustomerMsgResponseNews;
import com.cn.thinkx.wechat.base.wxapi.vo.CustomerMsgResponseVo;
import com.cn.thinkx.wechat.base.wxapi.vo.MsgRequest;
import com.cn.thinkx.wechat.base.wxapi.vo.MsgResponseNews;
import com.cn.thinkx.wechat.base.wxapi.vo.MsgResponseText;

/**
 * 消息builder工具类
 */
public class WxMessageBuilder {
	
	//客服文本消息
	public static String prepareCustomText(String openid,String content){
		JSONObject jsObj = new JSONObject();
		jsObj.put("touser", openid);
		jsObj.put("msgtype", MsgType.Text.name());
		JSONObject textObj = new JSONObject();
		textObj.put("content", content);
		jsObj.put("text", textObj);
		return jsObj.toString();
	}
	
	
	//获取 MsgResponseNews 对象
	public static String prepareCustomerNews(String openid ,List<MsgNews> msgNews){
		if(msgNews != null && msgNews.size() > 0){
			CustomerMsgResponseNews responseNews = new CustomerMsgResponseNews();
			responseNews.setTouser(openid);
			responseNews.setMsgtype(MsgType.News.getName());
			List<ArticleInfo> articles = new ArrayList<ArticleInfo>(msgNews.size());
			for(MsgNews n : msgNews){
				ArticleInfo a = new ArticleInfo();
				a.setTitle(n.getTitle());
				a.setPicurl(n.getPicpath());
				if(StringUtils.isEmpty(n.getFromurl())){
					a.setUrl(n.getUrl());
				}else{
					a.setUrl(n.getFromurl());
				}
				a.setDescription(n.getBrief());
				articles.add(a);
			}
			
			CustomerMsgResponseVo voNews=new CustomerMsgResponseVo();
			voNews.setArticles(articles);
			
			responseNews.setNews(voNews);
			return JSONObject.toJSONString(responseNews);
		}else{
			return "";
		}
	}
	
	//获取 MsgResponseText 对象
	public static MsgResponseText getMsgResponseText(MsgRequest msgRequest,MsgText msgText){
		if(msgText != null){
			MsgResponseText reponseText = new MsgResponseText();
			reponseText.setToUserName(msgRequest.getFromUserName());
			reponseText.setFromUserName(msgRequest.getToUserName());
			reponseText.setMsgType(MsgType.Text.toString());
			reponseText.setCreateTime(new Date().getTime());
			reponseText.setContent(msgText.getContent());
			return reponseText;
		}else{
			return null;
		}
	}
	
	//获取 MsgResponseNews 对象
	public static MsgResponseNews getMsgResponseNews(MsgRequest msgRequest,List<MsgNews> msgNews){
		if(msgNews != null && msgNews.size() > 0){
			MsgResponseNews responseNews = new MsgResponseNews();
			responseNews.setToUserName(msgRequest.getFromUserName());
			responseNews.setFromUserName(msgRequest.getToUserName());
			responseNews.setMsgType(MsgType.News.toString());
			responseNews.setCreateTime(new Date().getTime());
			responseNews.setArticleCount(msgNews.size());
			List<Article> articles = new ArrayList<Article>(msgNews.size());
			for(MsgNews n : msgNews){
				Article a = new Article();
				a.setTitle(n.getTitle());
				a.setPicUrl(n.getPicpath());
				if(StringUtils.isEmpty(n.getFromurl())){
					a.setUrl(n.getUrl());
				}else{
					a.setUrl(n.getFromurl());
				}
				a.setDescription(n.getBrief());
				articles.add(a);
			}
			responseNews.setArticles(articles);
			return responseNews;
		}else{
			return null;
		}
	}
	
}
