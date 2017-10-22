package com.jt.manage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jt.common.mapper.SysMapper;
import com.jt.manage.pojo.Item;

//当前接口 继承了SysMapper 继承了对单表crud操作
public interface ItemMapper extends SysMapper<Item>{
	
	public List<Item> findItemList(); //查询当前商品信息
	
	//根据分页查询数据
	public List<Item> findItemPageList(@Param("start")int start,@Param("rows")int rows);
	
	//根据商品分类号 查询分类名称
	public String findItemCatNameByItemId(Long itemId);
	
	//批量修改商品状态
	public void updateItemStatus(@Param("ids")Long[] ids,@Param("status")int status);
}
