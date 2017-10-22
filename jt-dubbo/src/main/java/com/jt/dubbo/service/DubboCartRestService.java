package com.jt.dubbo.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;
import com.jt.dubbo.pojo.Cart;

@Path("cart")
@Consumes({MediaType.APPLICATION_JSON,MediaType.TEXT_XML})
@Produces({ContentType.APPLICATION_JSON_UTF_8,ContentType.TEXT_XML_UTF_8})
public interface DubboCartRestService {
	
	//http://cart.jt.com//cart/query/
	@GET
	@Path("query")
	List<Cart> myCartList(@QueryParam(value="userId")Long userId);

	@POST
	@Path("save")
	void saveCart(@QueryParam(value="userId")Long userId,
			@QueryParam(value="itemId")Long itemId, 
			@QueryParam(value="num")Integer num, 
			@QueryParam(value="itemTitle")String itemTitle, 
			@QueryParam(value="itemImage")String itemImage, 
			@QueryParam(value="itemPrice")Long itemPrice);

	@GET
	@Path("update/num")
	void updateNum(@QueryParam(value="userId")Long userId,
			@QueryParam(value="itemId")Long itemId,
			@QueryParam(value="num")Integer num);

	@GET
	@Path("delete")
	void delete(@QueryParam(value="userId")Long userId, 
			@QueryParam(value="itemId")Long itemId);
	

}
