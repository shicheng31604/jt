package com.jt.web.threadlocal;

public class UserThreadLocal {
	
	private static final ThreadLocal<Long> USERID=new ThreadLocal<>();
	
	//向ThreadLocal里设置userid信息
	public static void setUserId(Long UserId){
		USERID.set(UserId);
	}
	
	//从ThreadLocal里获取userid信息
	public static Long getUserId(){
		return USERID.get();
	}

}
