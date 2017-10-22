package com.jt.order.mq.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.jt.dubbo.pojo.Order;
import com.jt.order.mapper.OrderMapper;

public class RabbitOrderService {
	
	@Autowired
	private OrderMapper orderMapper;
	
	
	
	public void create(Order order){
		orderMapper.createOrder(order);
		
		
	}

}
