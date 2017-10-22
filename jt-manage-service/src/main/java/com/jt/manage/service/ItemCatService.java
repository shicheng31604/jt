package com.jt.manage.service;

import java.util.List;

import com.jt.common.vo.ItemCatResult;
import com.jt.manage.pojo.ItemCat;

public interface ItemCatService {
	
	//根据父Id查询全部的子节点
	public List<ItemCat> findItemCatList(Long parentId);

	public ItemCatResult jsonp();

	

}
