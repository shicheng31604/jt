package com.jt.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.common.util.CookieUtils;
import com.jt.common.vo.SysResult;
import com.jt.web.pojo.User;
import com.jt.web.service.UserSerivce;

/**
 * 用来响应登录和注册的请求，并转向相关的前台页面
 * @author ysq
 *
 */
@Controller
public class UserController {
	
	@Autowired
	private UserSerivce userService;
	
	/*
	 * 转向登录页面
	 */
	@RequestMapping("/user/login")
	public String goLogin(){
		return "login";
	}
	
	/*
	 * 转向注册页面
	 */
	@RequestMapping("/user/register")
	public String goRegister(){
		return "register";
	}
	
	@RequestMapping("/user/doRegister")
	@ResponseBody
	public SysResult doRegister(User user){
		try {
			userService.doRegister(user);
			return SysResult.oK();
		} catch (Exception e) {
			return SysResult.build(201,"用户注册时失败");
		}
		
	}
	
	@RequestMapping("/user/doLogin")
	@ResponseBody
	public SysResult doLogin(User user,HttpServletRequest request,HttpServletResponse response){
		String ticket=userService.login(user);
		//Cookie的属性名要和jt.js的属性名对应上，是:JT_TICKET
		CookieUtils.setCookie(request, response,"JT_TICKET", ticket);
		return SysResult.oK();
	}
	
	@RequestMapping("/user/logout")
	public String logOut(HttpServletRequest request,HttpServletResponse response){
		CookieUtils.deleteCookie(request, response, "JT_TICKET");
		//登出后转向首页
		return "index";
	}

}
