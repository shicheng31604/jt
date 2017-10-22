package com.jt.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.ctc.wstx.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.HttpClientService;
import com.jt.common.util.CookieUtils;
import com.jt.web.pojo.User;
import com.jt.web.threadlocal.UserThreadLocal;
import com.mysql.jdbc.StringUtils;

import de.ruedigermoeller.serialization.annotations.Serialize;

public class OrderInterceptor implements HandlerInterceptor{
	
	@Autowired
	private HttpClientService httpClientService;
	
	private static final ObjectMapper MAPPER=new ObjectMapper();

	/* 
	 *进入Controller方法前执行
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//获取用户的ticket
		String ticket=CookieUtils.getCookieValue(request,"JT_TICKET");
		//下一步，根据ticket去SSO系统，从redis缓存里，取出ticket对应的用户信息的json串
		//因为涉及到跨域访问，所以这里我们用HttpClient来做
		String url="http://sso.jt.com/user/query/"+ticket;
		//resultJson的数据对应的是：SysResult.ok(用户的json串) 的json串
		String resultJson=httpClientService.doGet(url);
		
		if(StringUtils.isNullOrEmpty(resultJson)){
			//如果数据为空，证明用户未登陆,转向登录页面
			response.sendRedirect("/user/login.html");
			//在拦截器里，return false 意味着不会放行，走到controller方法里
			return false;
		}
			//进入此分支，说明用户已登录，则获取用户信息的json串
			String userJson=MAPPER.readTree(resultJson).get("data").asText();
		if(StringUtils.isNullOrEmpty(userJson)){
			//如果数据为空，证明用户未登陆,转向登录页面
			response.sendRedirect("/user/login.html");
			//在拦截器里，return false 意味着不会放行，走到controller方法里
			return false;
		}	
	
		User user=MAPPER.readValue(userJson, User.class);
		
		if(user ==null){
			//如果数据为空，证明用户未登陆,转向登录页面
			response.sendRedirect("/user/login.html");
			//在拦截器里，return false 意味着不会放行，走到controller方法里
			return false;
		}
		//获取userId
		Long userId=user.getId();
		
		//向ThreadLocal里存储userId信息
		UserThreadLocal.setUserId(userId);
		
		//放行
		return true;
		
		
		
	}
	/* 
	 *Controller方法执行后执行
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	/* 
	 *Controller方法执行后，转向页面前执行，一般是做资源清理工作
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
