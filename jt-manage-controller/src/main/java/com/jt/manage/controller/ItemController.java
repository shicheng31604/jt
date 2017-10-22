package com.jt.manage.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.vo.EasyUIResult;
import com.jt.common.vo.SysResult;
import com.jt.manage.pojo.Item;
import com.jt.manage.pojo.ItemDesc;
import com.jt.manage.service.ItemService;

@Controller
@RequestMapping("/item")
public class ItemController {
	
	//引入日志工具类
	private static Logger logger = Logger.getLogger(ItemController.class);
	
	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/findItem")
	@ResponseBody		//将返回数据转化为JSON串
	public List<Item>findItemList(){
		
		return itemService.findItemList();
	}
	
	
	
	/**
	 *http://localhost:8091/item/query?page=1&rows=20
	 *String 类型的返回值 会经过视图解析器 进行页面跳转  
	 * EasyUI要求的JSON参数 {"total":2000,"rows":[{},{},{}]}  
	 * 通过Response注解 可以将对象转化为JSON串    对象：total(int),rows(List<item>)
	*/
	
	@RequestMapping("/query")
	@ResponseBody 
	public EasyUIResult findItemPageList(int page,int rows){
		
		EasyUIResult result = itemService.findItemPageList(page,rows);
		return result;
	}
	
	/**
	 * 当前方法调用是ajax参数调用，可以直接返回，也可以通过response对象返回
	 * @param itemId
	 */
	/*@RequestMapping("/cat/queryItemName")  
	public void findItemCatName(Long itemId,HttpServletResponse response){
		
		response.setContentType("text/html;charset=utf-8");
		try {
			//根据商品分类ID查询商品名称
			String name = itemService.findItemCatNameByItemId(itemId);
			
			//通过response对象 实现数据的返回
			response.getWriter().write(name);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}*/
	
	
	@RequestMapping(value="/cat/queryItemName",produces="text/html;charset=utf-8")
	@ResponseBody
	public String findItemCatName(Long itemId){
		
		return itemService.findItemCatNameByItemId(itemId);
	}
	
	
	//测试通用Mapper中查询全部记录数
	@RequestMapping("/findMapperCount")
	@ResponseBody
	public int findMapperCount(){
		
		return itemService.findMapperCount();
	}
	
	
	//商品新增
	//由于商品描述信息和商品信息是一对一的关系，所以应该同时新增   页面传递Desc属性
	@RequestMapping(value="/save",produces="text/html;charset=utf-8")
	@ResponseBody
	public SysResult saveItem(Item item,String desc){
		
		try {
			//为了满足事务的一致性  需要在业务层进行处理
			itemService.saveItem(item,desc);
			
			//System.out.println("新增商品成功");
			logger.info("{新增商品成功}");  //执行更快
			return SysResult.build(200, "新增商品成功"); //给用户返回
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return SysResult.build(201, "商品新增失败 ");
		}
		
	}
	
	
	//商品修改   
	//商品描述信息的修改
	@RequestMapping("/update")
	@ResponseBody
	public SysResult updateItem(Item item,String desc){
		
		try {
			itemService.updateItem(item,desc);
			
			logger.info("{商品修改成功}");  //执行更快
			return SysResult.build(200, "商品修改成功"); //给用户返回
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return SysResult.build(201, "商品修改失败 ");
		}
	}
	
	
	//商品删除
	@RequestMapping("/delete")
	@ResponseBody
	public SysResult deleteItem(Long[] ids){
		try {
			itemService.deleteItems(ids);
			
			logger.info("{商品删除成功}");  //执行更快
			return SysResult.build(200, "商品删除成功"); //给用户返回
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return SysResult.build(201, "商品删除失败 ");
		}
	}
	
	
	//商品下架：  /item/instock
	@RequestMapping("/instock")
	@ResponseBody
	public SysResult updateInstock(Long[] ids){
		
		try {
			int status = 2;  //下架
			itemService.updateItemStatus(ids,status);
			
			logger.info("{商品下架成功}");  //执行更快
			return SysResult.build(200, "商品下架成功"); //给用户返回
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return SysResult.build(201, "商品下架失败 ");
		}
	}
	
	
	
	//商品上架  $.post("/item/reshelf")
	@RequestMapping("/reshelf")
	@ResponseBody
	public SysResult updateReshelf(Long[] ids){
		
		try {
			int status = 1;  //下架
			itemService.updateItemStatus(ids,status);
			logger.info("{商品上架成功}");  //执行更快
			return SysResult.build(200, "商品上架成功"); //给用户返回
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return SysResult.build(201, "商品上架失败 ");
		}
	}

	
	/**
	 * 实现商品描述信息的回显操作
	 * http://localhost:8091/item/query/item/desc/1474391990
	 */
	@RequestMapping("/query/item/desc/{itemId}")
	@ResponseBody
	public SysResult findItemDescByItemId(@PathVariable Long itemId){
		
		try {
			ItemDesc desc = itemService.findItemDesc(itemId);
			logger.info("{商品描述信息查询成功}");  //执行更快
			
			
			return SysResult.build(200, "商品描述信息查询成功", desc);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return SysResult.build(201, "商品描述信息查询失败");
		}
		
	
		
		
	}
	
	
	
	
}
