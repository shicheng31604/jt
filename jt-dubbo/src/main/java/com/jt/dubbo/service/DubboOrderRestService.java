package com.jt.dubbo.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;
import com.jt.dubbo.pojo.Order;

@Path("order")
@Consumes({MediaType.APPLICATION_JSON,MediaType.TEXT_XML})
@Produces({ContentType.APPLICATION_JSON_UTF_8,ContentType.TEXT_XML_UTF_8})
public interface DubboOrderRestService {
	
	@GET
	@Path("create")
	String createOrder(@QueryParam(value="json")String json);

	@GET
	@Path("query")
	Order queryOrderById(@QueryParam(value="id")String id);

}
