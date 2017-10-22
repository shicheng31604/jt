package com.jt.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jt.dubbo.pojo.Cart;
import com.jt.dubbo.service.DubboCartRestService;
import com.jt.web.threadlocal.UserThreadLocal;


@Controller
public class CartController {
	
	//调用的是Dubbo暴露的购物车服务接口
	@Autowired
	private DubboCartRestService dubboCartRestService;
	
	/*
	 * ①通过这个方法，转向购物车页面，页面对应的：WEB-INF/views/cart.jsp
	 * ②通过Model对象，将List<Cart>返回给前台，属性名cartList
	 * ③购物车属于某个用户的，我们需要根据用户id去查，今天的业务用户id写死
	 */
	@RequestMapping("/cart/show")
	public String myCartList(Model model){
		//后期会通过SSO获取登录的用户id
		Long userId=UserThreadLocal.getUserId();
		List<Cart> cartList=dubboCartRestService.myCartList(userId);
		model.addAttribute("cartList", cartList);
		//定位到cart.jsp页面
		return "cart";
		
	}
	/*
	 * 用来响应进入商品详情页后，添加购物车的请求，需要转向cart.jsp页面
	 * 需要接收相关商品参数，包含itemTitle,itemImage,itemPrice,num(购买的商品数量)
	 * 
	 */
	@RequestMapping("/cart/add/{itemId}")
	public String addCart(@PathVariable Long itemId,Integer num,String itemTitle,String itemImage,Long itemPrice){
		Long userId=UserThreadLocal.getUserId();
		dubboCartRestService.saveCart(userId,itemId,num,itemTitle,itemImage,itemPrice);
		
		//重定向到cart.jsp页面
		return "redirect:/cart/show.html";
	}
	/*
	 * 用来响应购物车商品数量修改的，需要转向cart.jsp页面
	 */
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	public String updateCart(@PathVariable Long itemId,@PathVariable Integer num){
		Long userId=UserThreadLocal.getUserId();
		dubboCartRestService.updateNum(userId,itemId,num);
		return "redirect:/cart/show.html";
		
	}
	/*
	 * 用来响应购物车商品删除，删除完之后，转向cart.jsp
	 */
	@RequestMapping("/cart/delete/{itemId}")
	public String delete(@PathVariable Long itemId){
		Long userId=UserThreadLocal.getUserId();
		dubboCartRestService.delete(userId,itemId);
		return "redirect:/cart/show.html";
		
	}
	/*
	 * 用来响应购物车-订单结算页面的跳转，对应的是 ：order-cart.jsp
	 * 此外，需要通过Model,将List<Cart>返回给前台，属性名carts
	 */
	@RequestMapping("/order/create")
	public String cartOrder(Model model){
		Long userId=UserThreadLocal.getUserId();
		List<Cart> cartList=dubboCartRestService.myCartList(userId);
		model.addAttribute("carts", cartList);
		
		return "order-cart";
	}
}
