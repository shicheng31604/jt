package com.jt.sso.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.BaseService;
import com.jt.sso.mapper.UserMapper;
import com.jt.sso.pojo.User;

import redis.clients.jedis.JedisCluster;

@Service
public class UserService extends BaseService<User>{
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private JedisCluster jedisCluster;
	
	private static final ObjectMapper MAPPER=new ObjectMapper();
	
	/*
	 * 根据param 和 type 去数据查询数据
	 * 当param是用户名时， select count(*) from tb_user where username=
	 * 当param是手机时，  select count(*) from tb_user where phone=
	 */
	public Boolean check(String param, Integer type) {
		Map<String,String> map=new HashMap<>();
		if(1==type){
			map.put("type","username");
		}else if(2==type){
			map.put("type","phone");
		}else{
			map.put("type","email");
		}
		
		map.put("param", param);
		//在userMapper.xml里
		//注意，对type的取值，用$取，避免出现 ' '
		// select count(*) from tb_user where ${type}=#{param}
		int count=userMapper.check(map);
		System.out.println(param+":"+type);
		System.out.println(count);
		if(count==0){
			//表示可注册 
			return false;
		}else{
			return true;
		}
		
		
	}

	public void register(User user) {
		//对注册的密码进行加密。
		user.setPassword(DigestUtils.md5Hex(user.getPassword()));
		//为了避免邮箱检验的唯一性出错
		user.setEmail(user.getPhone());
		user.setCreated(new Date());
		user.setUpdated(user.getCreated());
		userMapper.insert(user);
		
	}
	
	/*
	 * 用于做用户登录的校验
	 * 实现思路：
	 * ①根据u(用户输出的登录名)，去数据库查询，假设得到的对象db_user
	 * ②对用户提交的明码做加密，然后和db_user的password做比对
	 * ③如果校验通过，生成用户登录票根（ticket)
	 * ④将ticket存到redis缓存里
	 * ⑤return ticket
	 * 
	 * 
	 */
	public String login(String u, String p) throws Exception {
		User user=new User();
		user.setUsername(u);
		//调用BasicService的方法，根据条件，查询出单个对象
		User db_user=super.queryByWhere(user);
		
		if(DigestUtils.md5Hex(p).equals(db_user.getPassword())){
			//如果进入此判断，说明用户登录校验成功，生成票根
			//票根的生成算法不唯一，可以自己确定
			String ticket=DigestUtils.md5Hex("TICKET_"+u);
			//写缓存，key是ticket,value是用户对象的json串
			jedisCluster.set(ticket,MAPPER.writeValueAsString(db_user));
			
			return ticket;
			
		}
		return null;
	}

}
