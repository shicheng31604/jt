package com.jt.manage.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.vo.ItemCatData;
import com.jt.common.vo.ItemCatResult;
import com.jt.manage.mapper.ItemCatMapper;
import com.jt.manage.pojo.ItemCat;
import com.mysql.jdbc.StringUtils;

import redis.clients.jedis.JedisCluster;

@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private ItemCatMapper itemCatMapper;
	
	/*
	 * 这是common提供的redis工具类，
	 * 底层是通过jedis来实现的
	 * 提供了set(k,v) get(k,v) del(k)等方法
	 * 此外，已支持jedis的shard分片。
	 */
	
	@Autowired
	private JedisCluster jedisCluster;
	
	
	//private JedisCluster jedisCluster;
	
	private static final ObjectMapper MAPPER=new ObjectMapper();
	
	
	
	/*
	 * 实现思路：
	 * 商品类目查询，引入缓存
	 * ①首次查询时，因为缓存里没有数据，所以去数据库查询
	 * ②将数据查出，返回给前台并将数据存在缓存里
	 * ③缓冲里数据的存储形式，k-v，k是商品分类的id，v是List<ItemCat>的json串
	 * ④当从缓存里取数据的时候，通过k取，取出对应的json串，再转成List<ItemCat>传给前台
	 * 
	 */
	@Override
	public List<ItemCat> findItemCatList(Long parentId) {
		//做业务key的标识，方便区别与其他业务
		String ITEM_CAT_KEY="ITEM_CAT_"+parentId;
		//去缓存里根据key查对应的value值，实际是List<ItemCat>的json串
		String resultJson=jedisCluster.get(ITEM_CAT_KEY);
		
		ItemCat itemCat = new ItemCat();
		itemCat.setParentId(parentId);
		
		try {
			
			//进入此判断，则证明是首次查询，缓存里没有数据，所以去数据查
			if(StringUtils.isNullOrEmpty(resultJson)){
				//去数据库查询
				List<ItemCat> resultList=itemCatMapper.select(itemCat);
				
				//将List<ItemCat>以json串的方式存在缓存里
				jedisCluster.set(ITEM_CAT_KEY,MAPPER.writeValueAsString(resultList));
				
				return resultList;
			}else{
				//进入到这个判断，说明缓存里有数据，
				//我们需要把resultJson转变为List<ItemCat>传给前台
				List<ItemCat> resutlist=MAPPER.readValue(MAPPER.readTree(resultJson).traverse(),
						MAPPER.getTypeFactory().constructCollectionType(List.class, ItemCat.class));
				return resutlist;
			}
		} catch (Exception e) {
			//如果缓存出问题，需要去数据库查询
			return  itemCatMapper.select(itemCat);
		}
		
		
		
		
	}


	/*
	 * 
	 *  {"data":[{"u":"/products/1(商品的id号）.html","n":"<a href='/products/1.html'>图书、音响、电子书刊(商品分类名)</a>", 
	i(第二级子菜单):[{"u":"/products/2.html",n:"电子书刊", 
	i(第三级子菜单):["/products/4.html|网络原创","/products/5.html|数字杂志","/product/6.html|多媒体图书"]
	 */
	@Override
	public ItemCatResult jsonp() {
		//查询所有的商品分类
		List<ItemCat> LIST_ALL=itemCatMapper.select(null);
		
		//存放的是一级商品分类数据
		List<ItemCatData> LEVEL_1=new ArrayList<>();
		
		for(ItemCat L1:LIST_ALL){
			//筛选一级商品分类
			if(L1.getParentId()==0){
				ItemCatData ICD_1=new ItemCatData();
				//[{"u":"/products/1(商品的id号）.html",
				ICD_1.setUrl("/products/"+L1.getId()+".html");
				//"n":"<a href='/products/1.html'>图书、音响、电子书刊(商品分类名)</a>"
				ICD_1.setName("<a href='/products/"+L1.getId()+".html'>"
				+L1.getName()+"</a>");
				//存放的是当前一级商品分类对应的二级子集合
				List<ItemCatData> LEVEL_2=new ArrayList<>();
				ICD_1.setItems(LEVEL_2);
				
				LEVEL_1.add(ICD_1);
				
				//select * from tb_item_cat where parent_id=一级商品分类id
				//L2_LIST封装的是一级商品分类对应的二级分类
				List<ItemCat> L2_LIST=itemCatMapper.findItemCatByParentId(L1.getId());
				
				for(ItemCat L2:L2_LIST){
					//[{"u":"/products/2.html",n:"电子书刊", 
					ItemCatData ICD_2=new ItemCatData();
					ICD_2.setUrl("/products/"+L2.getId()+".html");
					ICD_2.setName(L2.getName());
					
					//因为三级的json要求的格式是：["/products/4.html|网络原创"
					//所以List里的元素必须是String类型
					List<String> LEVEL_3=new ArrayList<>();
					ICD_2.setItems(LEVEL_3);
					
					LEVEL_2.add(ICD_2);
					
					//获取二级商品分类下的三级分类
					List<ItemCat> L3_LIST=itemCatMapper.findItemCatByParentId(L2.getId());
					for(ItemCat L3:L3_LIST){
						LEVEL_3.add("/products/"+L3.getId()+".html|"+L3.getName());
					}
				}
			}
		}
		
		
		ItemCatResult result=new ItemCatResult();
		result.setItemCats(LEVEL_1);
		return result;
	}


	
}
