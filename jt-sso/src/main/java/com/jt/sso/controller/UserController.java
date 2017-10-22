package com.jt.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.common.vo.SysResult;
import com.jt.sso.pojo.User;
import com.jt.sso.service.UserService;

import redis.clients.jedis.JedisCluster;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JedisCluster jedisCluster;
	
	/*
	 * 1.用来响应用户在注册时，前台发起的校验响应。
	 * 这个请求是跨域请求，所在在接参的时候，需要接
	 * callback参数 
	 * 
	 * 2.type=1,表示传来的是用户名
	 * type=2,表示传来的是手机
	 * type=3,表示传来的是邮箱
	 * 
	 * 3.因为是jsonp的跨域请求，所以返回数据的形式：
	 * callback( json )
	 * 其中json 可以通过京淘提供SysResult.oK( )来构造
	 * 如果SysResult.oK(false) 表示可以注册
	 * 如果SysResult.oK(true) 表示不可注册
	 * 所以这个false或true的结果需要通过去查询数据库
	 */
	@RequestMapping("/user/check/{param}/{type}")
	@ResponseBody
	public Object check(String callback,@PathVariable String param,@PathVariable Integer type){
		try {
			Boolean b=userService.check(param,type);
			MappingJacksonValue MJV=new MappingJacksonValue(SysResult.oK(b));
			MJV.setJsonpFunction(callback);
			return MJV;
		} catch (Exception e) {
			return SysResult.build(201,"校验信息时出错");
		}
	}
	
	@RequestMapping("/user/register")
	@ResponseBody
	public SysResult register(User user){
		userService.register(user);
		return SysResult.oK();
	}
	
	@RequestMapping("/user/login")
	@ResponseBody
	public SysResult login(String u,String p){
		try {
			String ticket=userService.login(u,p);
			return SysResult.oK(ticket);
			
		} catch (Exception e) {
			return SysResult.build(201,"用户登录失败");
		}
	}
	/*
	 * 用来响应前台cookie发来的票根查询请求
	 * 此外，这个是jsonp的跨域调用，需要接收callback参数
	 * 实现思路：
	 * ①当controller拿到ticket之后，会去redis里查对应的value(用户信息的json串)
	 * ②组织成 callback(SysResult.ok(用户的json串) )
	 * 
	 */
	@RequestMapping("/user/query/{ticket}")
	@ResponseBody
	public Object queryByTicket(String callback,@PathVariable String ticket){
		String json=jedisCluster.get(ticket);
		MappingJacksonValue MJV=new MappingJacksonValue(SysResult.oK(json));
		MJV.setJsonpFunction(callback);
		return MJV;
		
	}

}
