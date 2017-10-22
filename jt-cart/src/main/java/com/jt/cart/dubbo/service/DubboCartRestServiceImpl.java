package com.jt.cart.dubbo.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.jt.cart.mapper.CartMapper;
import com.jt.dubbo.pojo.Cart;
import com.jt.dubbo.service.DubboCartRestService;

/**
 * 当provider提供服务后，消费的时候报错：check provider if start
 * 处理办法：停掉dubbo(听到dubbo控制台),进入zookeeper的客户端，删除 /dubbo节点  rmr /dubbo(递归删除）
 * ——>启动dubbo——>启动购物——>启动前台测试
 * 
 * 停掉Zookeeper服务器：进入zookeeper的bin目录下，执行：sh zkServer.sh stop
 * @author ysq
 *
 */
public class DubboCartRestServiceImpl implements DubboCartRestService{

	@Autowired
	private CartMapper cartMapper;

	//根据传来的用户id,查询出List<Cart>返回
	@Override
	public List<Cart> myCartList(Long userId) {
		Cart cart=new Cart();
		cart.setUserId(userId);
		List<Cart> cartList=cartMapper.select(cart);
		
		return cartList;
	}
	
	//实现用户的购物车商品新增或保存
	//实现思路：先要根据用户id和商品id来判断 ：购物车里是否已存在此商品
	//select * from tb_cart where userId=#{userId} and itemId=#{itemId}
	//分支一，如果存在，做商品数量的累加
	//分支二，如果不存在，做商品的新增
	@Override
	public void saveCart(Long userId, Long itemId, Integer num, String itemTitle, String itemImage, Long itemPrice) {
		Cart cart=new Cart();
		cart.setUserId(userId);
		cart.setItemId(itemId);
		
		int count=cartMapper.selectCount(cart);
		if(count==1){
			//说明此商品在购物车里已存在，做数量的累加
			//拿到数据库里的cart对象
			Cart db_cart=cartMapper.select(cart).get(0);
			
			db_cart.setNum(db_cart.getNum()+num);
			db_cart.setUpdated(new Date());
			
			cartMapper.updateByPrimaryKey(db_cart);
			
		}else{
			//说明此商品是新商品，我们需要加入到购物车里
			cart.setItemTitle(itemTitle);
			cart.setItemImage(itemImage);
			cart.setItemPrice(itemPrice);
			cart.setNum(num);
			cart.setCreated(new Date());
			cart.setUpdated(cart.getUpdated());
			//插入到tb_cart表里
			cartMapper.insert(cart);
		}
		
		
	}
	
	/*
	 * 实现购物车商品数量的更新
	 * update tb_cart set num=#{num},updated=#{updated}
	 * where userId=#{userId} and itemId=#{itemId}
	 * 
	 */
	@Override
	public void updateNum(Long userId, Long itemId, Integer num) {
		Cart cart=new Cart();
		cart.setUserId(userId);
		cart.setItemId(itemId);
		cart.setNum(num);
		cart.setUpdated(new Date());
		
		//自定义的更新购物车商品数量的方法
		cartMapper.updateCart(cart);
		
	}
	
	/*
	 * 
	 * 根据用户id和商品id删除购物车商品
	 */
	@Override
	public void delete(Long userId, Long itemId) {
		Cart cart=new Cart();
		cart.setUserId(userId);
		cart.setItemId(itemId);
		cartMapper.delete(cart);
		
	}

	
}
