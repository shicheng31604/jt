package com.jt.manage.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.manage.pojo.Item;
import com.jt.manage.service.ItemService;

/**
 * 用来响应前台传来的商品详情的请求
 * @author ysq
 *
 */
@Controller
public class WebItemController {
	
	@Autowired
	private ItemService itemService;

	@RequestMapping("/web/item/{itemId}")
	@ResponseBody
	public Item queryItem(@PathVariable Long itemId){
		//根据商品id查询商品信息，并返回item对象，
		//然后通过@ResponseBody转成json串返回给前台
		return itemService.findItemById(itemId);
	}
}
