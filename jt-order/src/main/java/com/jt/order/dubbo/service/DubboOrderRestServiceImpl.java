package com.jt.order.dubbo.service;

import java.util.Date;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.dubbo.pojo.Order;
import com.jt.dubbo.service.DubboOrderRestService;
import com.jt.order.mapper.OrderMapper;

public class DubboOrderRestServiceImpl implements DubboOrderRestService{
	
	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	
	private static final ObjectMapper MAPPER=new ObjectMapper();

	@Override
	public String createOrder(String json) {
		try {
			Order order=MAPPER.readValue(json, Order.class);
			//生成订单编号，用户id+当前的时间戳
			String orderId=order.getUserId()+""+System.currentTimeMillis();
			order.setOrderId(orderId);
			order.setCreated(new Date());
			order.setUpdated(order.getCreated());
			
			order.setStatus(1);//1表示未支付
			
			//向数据库里插入oder对象的数据，这里会用到mybatis的批量新增
			//向tb_order,tb_order_shipping,tb_order_item;
			//orderMapper.createOrder(order);
			rabbitTemplate.convertAndSend("shicheng.send", order);
			
			
		
			//向前台返回生成订单编号，因为前台要构造SysResult.ok(orderId)这种形式
			return orderId;
			
		} catch (Exception e) {
			e.printStackTrace();
		
		}return null;
	}

	@Override
	public Order queryOrderById(String id) {
		
		return orderMapper.queryOrderById(id);
	}

}
