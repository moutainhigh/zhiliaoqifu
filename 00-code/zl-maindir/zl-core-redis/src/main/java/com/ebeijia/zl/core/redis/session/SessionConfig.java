package com.ebeijia.zl.core.redis.session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

//这个类用配置redis服务器的连接
//maxInactiveIntervalInSeconds为SpringSession的过期时间（单位：秒
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds= 3600)
public class SessionConfig {
	

	
}