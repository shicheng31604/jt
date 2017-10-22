package com.jt.manage.service;

import java.util.List;

import com.jt.common.vo.EasyUIResult;
import com.jt.manage.pojo.Item;
import com.jt.manage.pojo.ItemDesc;

public interface ItemService {
	
	public List<Item> findItemList();
	
	//根据指定的页面和条数 进行分页查询
	public EasyUIResult findItemPageList(int page, int rows);
	
	//根据商品分类Id查询商品名称
	public String findItemCatNameByItemId(Long itemId);
	
	//测试通用mapper
	public int findMapperCount();

	//public void saveItem(Item item);

	public void updateItem(Item item,String desc);

	public void deleteItems(Long[] ids);

	public void updateItemStatus(Long[] ids, int status);
	
	//表示同时新增 item  itemdesc表
	public void saveItem(Item item, String desc);
	
	//根据itemId查询商品描述对象
	public ItemDesc findItemDesc(Long itemId);

	//根据商品id查询商品
	public Item findItemById(Long itemId);

	
	
}
