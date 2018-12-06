package com.cn.thinkx.oms.security.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.cn.thinkx.oms.sys.model.User;
import com.cn.thinkx.oms.sys.service.UserService;
import com.ebeijia.zl.common.utils.constants.Constants;
import com.ebeijia.zl.common.utils.constants.Constants.RandomCodeType;
import com.ebeijia.zl.common.utils.tools.MD5Utils;
import com.ebeijia.zl.common.utils.tools.StringUtils;

public class MyUsernamePasswordAuthenticationFilter extends
		UsernamePasswordAuthenticationFilter {
	
	Logger logger = LoggerFactory.getLogger(MyUsernamePasswordAuthenticationFilter.class);
	
	public static final String VALIDATE_CODE = "authCode";  
    public static final String USERNAME = "username";  
    public static final String PASSWORD = "password";
    public static final String LOGIN_TYPE = "loginType";
    
    private int showCheckCode = 0;
    
	@Autowired
	@Qualifier("userService")
	private UserService userService;
	
 	public int getShowCheckCode() {
 		return showCheckCode;
	}

	public void setShowCheckCode(int showCheckCode) {
		this.showCheckCode = showCheckCode;
	}

	@Override  
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {  
       /* if (!request.getMethod().equals("POST")) {  
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());  
        } */ 
        HttpSession session = request.getSession();
        session = request.getSession(false);//false代表不创建新的session，直接获取当前的session
        
        //检测验证码  
        checkValidateCode(request);  

        String username = obtainUsername(request);  
        String password = obtainPassword(request);  
        String authCode = obtainValidateCodeParameter(request);
        String loginType = obtainLoginType(request);

          
        //验证用户账号与密码是否对应  
        username = username.trim();  
        User user = userService.getUserByName(null, username, loginType);
            
        if(user == null) {       	 
         	session.setAttribute("showCheckCode" ,"1" );  
         	session.setAttribute("SECURITY_LOGIN_EXCEPTION" ,"用户名或密码错误！" );  
         	logger.info("用户名或密码错误");
            throw new AuthenticationServiceException("用户名或密码错误！"); 
            
        }else if(user.getPassword()=="" || user.getPassword()==null){
        	session.setAttribute("showCheckCode" ,"1" );  
        	session.setAttribute("SECURITY_LOGIN_EXCEPTION" ,"用户名或密码错误！" ); 
        	
            throw new AuthenticationServiceException("用户名或密码错误！");
        } else if(!"0".equals(user.getDataStat())){
        	session.setAttribute("showCheckCode" ,"1" );  
        	session.setAttribute("SECURITY_LOGIN_EXCEPTION" ,"当前用户不存在，请重新输入登录名和密码" ); 
        	
            throw new AuthenticationServiceException("用户名或密码错误！");
        }
        
    	String sysMd5Password = MD5Utils.MD5(user.getPassword() + authCode);
        if (!user.getLoginName().equals(username) || !sysMd5Password.equals(password)) {
   	
         	session.setAttribute("showCheckCode" ,"1" ); 
         	session.setAttribute("SECURITY_LOGIN_EXCEPTION" ,
					"用户名或密码错误！" ); 
 	
            throw new AuthenticationServiceException("用户名或密码错误！");
        
        }else{
        	
        	if(session.getAttribute("showCheckCode")=="1"){
        	    session.setAttribute("showCheckCode" ,	"0" ); 
        	}
        }
        
        session.setAttribute(Constants.SESSION_USER, user);
    	session.removeAttribute("SECURITY_LOGIN_EXCEPTION");
    	session.removeAttribute("showCheckCode");
        //UsernamePasswordAuthenticationToken实现 Authentication  
        //这里要注意了，我第二个参数是用自己的md5加密了密码再去传参的，因为我的密码都是加密后存进数据库的。
        //如果这里不加密，那么和在数据库取出来的不匹配，最终即使登录账号和密码都正确，也将无法登录成功。
        //因为在AbstractUserDetailsAuthenticationProvider里还会对用户和密码验证，分别是
        //user = retrieveUser(username, (UsernamePasswordAuthenticationToken) authentication);//这个通过才能顺利通过
        //另一个是 additionalAuthenticationChecks(user, (UsernamePasswordAuthenticationToken) authentication);//如果retrieveUser方法验证不通过，将无法访问
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, user.getPassword());  
        // Place the last username attempted into HttpSession for views  
          
        // 允许子类设置详细属性  
        setDetails(request, authRequest);  
        // 运行UserDetailsService的loadUserByUsername 再次封装Authentication  
        return this.getAuthenticationManager().authenticate(authRequest);  
    }  
      
    protected void checkValidateCode(HttpServletRequest request) {   
        HttpSession session = request.getSession();  
          
        String sessionValidateCode = obtainSessionValidateCode(session);   
       
        if (StringUtils.isEmpty(sessionValidateCode)) {// 
        	session.setAttribute("SECURITY_LOGIN_EXCEPTION" ,"验证码失效" );    
        	throw new AuthenticationServiceException("验证码错误！");   
		}
        
        String authCode=obtainValidateCodeParameter(request);
        if (!authCode.equalsIgnoreCase(sessionValidateCode)) {
        	session.setAttribute("SECURITY_LOGIN_EXCEPTION" ,"验证码错误" );    
        	throw new AuthenticationServiceException("验证码错误！");   
        }
       
    }  
      
    private String obtainLoginType(HttpServletRequest request) {  
        Object obj = request.getParameter(LOGIN_TYPE);  
        return null == obj ? "" : obj.toString();  
    }
    
    private String obtainValidateCodeParameter(HttpServletRequest request) {  
        Object obj = request.getParameter(VALIDATE_CODE);  
        return null == obj ? "" : obj.toString();  
    }  
  
    protected String obtainSessionValidateCode(HttpSession session) {  
        Object obj = session.getAttribute(RandomCodeType.LOGIN.getCode());  
        return null == obj ? "" : obj.toString();  
    }  
  
    @Override  
    protected String obtainUsername(HttpServletRequest request) {  
        Object obj = request.getParameter(USERNAME);  
        return null == obj ? "" : obj.toString();  
    }  
  
    @Override  
    protected String obtainPassword(HttpServletRequest request) {  
        Object obj = request.getParameter(PASSWORD);  
        return null == obj ? "" : obj.toString();  
    }  
   
}  
