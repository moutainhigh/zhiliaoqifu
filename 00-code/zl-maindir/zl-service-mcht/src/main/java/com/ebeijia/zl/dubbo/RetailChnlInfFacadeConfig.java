package com.ebeijia.zl.dubbo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.dubbo.config.spring.ServiceBean;
import com.ebeijia.zl.facade.telrecharge.service.RetailChnlInfFacade;

/**
 * 服务暴露的接口 配置
 * @author zhuqiuyou
 *
 */
@Configuration
public class RetailChnlInfFacadeConfig extends DubboProviderConfig {

	
	@Bean
	public ServiceBean<RetailChnlInfFacade> retailChnlInfFacade(RetailChnlInfFacade retailChnlInfFacade) {
		
		ServiceBean<RetailChnlInfFacade> serviceBean=new ServiceBean<RetailChnlInfFacade>();
		serviceBean.setInterface(RetailChnlInfFacade.class.getName());
		serviceBean.setVersion("1.0.0");
		serviceBean.setRef(retailChnlInfFacade);
		serviceBean.setCluster("failfast");
		return serviceBean;
	}
}