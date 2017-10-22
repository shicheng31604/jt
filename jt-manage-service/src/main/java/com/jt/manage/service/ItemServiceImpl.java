package com.jt.manage.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jt.common.vo.EasyUIResult;
import com.jt.manage.mapper.ItemDescMapper;
import com.jt.manage.mapper.ItemMapper;
import com.jt.manage.pojo.Item;
import com.jt.manage.pojo.ItemDesc;
import com.mysql.jdbc.StringUtils;

import redis.clients.jedis.JedisCluster;
@Service
public class ItemServiceImpl implements ItemService {
	
	@Autowired
	private ItemMapper itemMapper;
	
	@Autowired
	private ItemDescMapper itemDescMapper;
	
	@Autowired
	private JedisCluster jedisCluster;
	
	private static final ObjectMapper MAPPER=new ObjectMapper();
	
	
	
	@Override
	public List<Item> findItemList() {
		
		return itemMapper.findItemList();
	}

	
	/**
	 * 分页的业务逻辑
	 * 0-20   SELECT * FROM tb_item ORDER BY updated DESC LIMIT 0,20   第1页
	 * 20-40  SELECT * FROM tb_item ORDER BY updated DESC LIMIT 20,20 第2页
	 * 40-60  SELECT * FROM tb_item ORDER BY updated DESC LIMIT 40,20 第3页
	 * 40-60  SELECT * FROM tb_item ORDER BY updated DESC LIMIT (page-1)* rows,20 第n页
	 * easyUI的页面格式要求   {"total":2000,"rows":[{},{},{}]}
	 */
	/*@Override
	public EasyUIResult findItemPageList(int page, int rows) {
		
		*//**
		 * JPA操作  item(id =1,name=2)
		 * 会自动根据对象中不为null的数据  拼接sql语句
		 * select * from tb_item where id = 1 and name = 2;
		 *//*
		
		int total = itemMapper.selectCount(null); //使用通用mapper的查询操作
		
		//表示分页后的数据
		int start = (page - 1) * rows;
		List<Item> itemList = itemMapper.findItemPageList(start,rows);
		
		
		return new EasyUIResult(total, itemList);
	}*/
	
	public EasyUIResult findItemPageList(int page, int rows) {
		//1.为分页插件赋值   指定页数 和每页展现记录总数
		PageHelper.startPage(page, rows);
		
		//2.必须在设定参数之后进行查询全部记录数的操作。否则分页插件将不能执行
		//因为分页插件自动会调用该方法 ，所以，查询的记录数的最终结果就是分页后的数据
		List<Item> itemList = itemMapper.findItemList();
		
		//通过分页Info会自己计算分页的记录数 和总数
		PageInfo<Item> info = new PageInfo<>(itemList);
		
		return new EasyUIResult(info.getTotal(), itemList);
	}


	@Override
	public String findItemCatNameByItemId(Long itemId) {
		
		return itemMapper.findItemCatNameByItemId(itemId);
	}


	@Override
	public int findMapperCount() {
		
		//itemMapper调用通用Mapper中的sql语句  
		//方法的路径：com.jt.manage.service.ItemServiceImpl.itemMapper.findMapperCount()
		return itemMapper.findMapperCount();
	}

	
	/**
	 * 1.问题分析：
	 * 当新增入库Item数据时，由于数据库是主键自增，所以在新增之后才能获取itemId
	 * 当时新增itemDesc时，必须要求id值，否则新增失败。
	 * 
	 * 2.解决办法 1  失败
	 * 	1.让item对象先入库操作，才能获取id
	 *  2.再次查询最大值  为itemDesc赋值    这样的方法在高并发的条件下 肯定有问题
	 *  
	 *  方法2：
	 *  将item对象整个当成where条件   在一定条件下 可以
	 *  
	 *  3.正解
	 *  	采用JPA的形式操作数据库后，会自动的将数据再次回显。
	 */
	@Override
	public void saveItem(Item item,String desc) {
		item.setStatus(1); //正常
		item.setCreated(new Date());
		item.setUpdated(item.getCreated()); //时间统一
		//动态的新增   新增主表信息
		itemMapper.insertSelective(item);
		
		//新增item_desc表中数据
		ItemDesc itemDesc = new ItemDesc();
		itemDesc.setItemId(item.getId());
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(item.getCreated());
		itemDesc.setUpdated(item.getUpdated());
		itemDescMapper.insert(itemDesc);
		
	}


	@Override
	public void updateItem(Item item,String desc) {
		
		item.setUpdated(new Date());
		itemMapper.updateByPrimaryKeySelective(item);
		
		ItemDesc itemDesc = new ItemDesc();
		itemDesc.setItemId(item.getId());
		itemDesc.setItemDesc(desc);
		itemDesc.setUpdated(item.getUpdated());
		
		itemDescMapper.updateByPrimaryKeySelective(itemDesc);
		
	}


	@Override
	public void deleteItems(Long[] ids) {
		
		itemDescMapper.deleteByIDS(ids);
		itemMapper.deleteByIDS(ids);
		
	}


	@Override
	public void updateItemStatus(Long[] ids, int status) {
		
		itemMapper.updateItemStatus(ids,status);
		
	}


	@Override
	public ItemDesc findItemDesc(Long itemId) {
		
		return itemDescMapper.selectByPrimaryKey(itemId);
	}


	@Override
	public Item findItemById(Long itemId) {
		String ITEM_KEY="ITEM_"+itemId;
		String resultJson=jedisCluster.get(ITEM_KEY);
		
		try {
			//进入这个判断，说明缓存里没有数据，需要去数据查询
			if(StringUtils.isNullOrEmpty(resultJson)){
				Item item=itemMapper.selectByPrimaryKey(itemId);
				//将商品对象的json串存入缓存
				jedisCluster.set(ITEM_KEY,MAPPER.writeValueAsString(item));
				
				return item;
			}else{
				//进入这个分支，说明缓存里有数据，所以需要将json串转变为item对象
				//返回给Controll层
				
				Item item=MAPPER.readValue(resultJson,Item.class);
				return item;
				
			}
		} catch (Exception e) {
			//如果缓存出现异常，去数据库查询
			return itemMapper.selectByPrimaryKey(itemId);
		}
		
		
		
		
	}


	

}
