package com.ebeijia.zl.shop.utils;

import com.cn.thinkx.ecom.redis.core.utils.JedisUtilsWithNamespace;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Session AOP切面
 */
@Component
@Aspect
public class SessionAop {

    @Autowired
    HttpServletRequest request;
    @Autowired
    HttpServletResponse response;
    @Autowired
    HttpSession session;

    @Autowired
    JedisUtilsWithNamespace jedis;

    Logger logger = LoggerFactory.getLogger(SessionAop.class);

    @Around(value = "@annotation(com.ebeijia.zl.shop.utils.TokenCheck)")
    public Object aroundManager(ProceedingJoinPoint pj) throws Throwable {
        //TODO 优化性能，避免执行不必要的逻辑
            String path = request.getContextPath();
            String fullpath = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort() + path + "/";
            MethodSignature signature = (MethodSignature) pj.getSignature();
            logger.info("[Path:" + path + "],[FullPath:" + fullpath + "]");
            logger.info("[signature:" + signature.toLongString() + "]");
            String token = getToken();
            if (token != null) {
                String s = jedis.get(token);
                logger.info("[user:" + s + "]");
                //TODO fake login user
                session.setAttribute("userId", s);
            }
        Object proceed = pj.proceed();
        return proceed;
    }

    private String getToken() {
        String token = request.getParameter("token");
        if (token != null) {
            return token;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName() == "token") {
                    return c.getValue();
                }
            }
        }
        return null;
    }


}
