package com.jt.web.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.HttpClientService;
import com.jt.web.pojo.User;

/**
 * 这个类用于前台通过httpclient向单点登录系统提交用户数据
 * @author ysq
 *
 */
@Service
public class UserSerivce {
	
	@Autowired
	private HttpClientService httpClientService;
	
	private static final ObjectMapper MAPPER=new ObjectMapper();

	public void doRegister(User user) throws Exception {
		String url="http://sso.jt.com/user/register";
		//通过map组织了表单数据，然后通过post提交给单点登录系统
		Map<String,String> map=new HashMap<>();
		map.put("username",user.getUsername());
		map.put("password",user.getPassword());
		map.put("phone",user.getPhone());
		map.put("email",user.getEmail());
		
		httpClientService.doPost(url,map,"utf-8");
		
	}

	public String login(User user) {
		String url="http://sso.jt.com/user/login";
		Map<String, String> map=new HashMap<>();
		
		//此处是按照京淘的接口文档来实现的
		map.put("u",user.getUsername());
		map.put("p",user.getPassword());
		try {
			//json是单点登录系统返回的SystResult.oK(ticket)的json串
			String json=httpClientService.doPost(url,map,"utf-8");
			JsonNode jn=MAPPER.readTree(json);
			//从json串里解析出ticket信息
			String ticket=jn.get("data").asText();
			return ticket;
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return null;
	}

}
