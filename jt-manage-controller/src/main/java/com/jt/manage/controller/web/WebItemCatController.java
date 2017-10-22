package com.jt.manage.controller.web;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.vo.ItemCatResult;
import com.jt.manage.service.ItemCatService;

/**
 * 这个类是用来响应前台商品分类展示的
 * @author ysq
 *
 */
@Controller
public class WebItemCatController {
	@Autowired
	private ItemCatService itemCatService;
	
	private static final ObjectMapper MAPPER=new ObjectMapper();
	
	/*
	 * 最后返回给前台的数据形式：category.getDataService(json)数据
	 * 关键是怎么构造json数据，京淘为此提供了对象类
	 * 1.第一个用的对象是Common工程提供的ItemCatResult
	 * 这个对象的作用是构造json串中的data 属性
	 * 2.第二个对象是ItemCatData对象
	 * 这个对象的作用是构造 u  n  i 属性，其中i要封装某一级商品分类的子级list
	 * 
	 */
	@RequestMapping("/itemcat/all")
	public void queryItemCatJsonp(String callback,HttpServletResponse response){
		ItemCatResult result=itemCatService.jsonp();
		try {
			String json=MAPPER.writeValueAsString(result);
			//拼接成jsonp的调用格式
			String resultJson=callback+"("+json+")";
			
			//因为前台发过来的请求是ajax请求，所以我们需要将数据返回到响应域里
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(resultJson);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
//	@RequestMapping("/web/itemcat/all")
//	@ResponseBody
//	public ItemCatResult queryItemCatJsonp(){
//		return itemCatService.jsonp();
//	}
	

}
