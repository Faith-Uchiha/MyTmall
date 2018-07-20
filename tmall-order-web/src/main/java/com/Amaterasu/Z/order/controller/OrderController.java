package com.Amaterasu.Z.order.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.Amaterasu.Z.cart.service.CartService;
import com.Amaterasu.Z.order.pojo.OrderInfo;
import com.Amaterasu.Z.order.service.OrderService;
import com.Amaterasu.Z.pojo.ResponseResult;
import com.Amaterasu.Z.pojo.TbItem;
import com.Amaterasu.Z.pojo.TbUser;

@Controller
public class OrderController {

	@Autowired 
	private CartService cartService;
	
	@Autowired
	private OrderService orderService;
	
	@RequestMapping(value="/order/order-cart")
	public String confireOrder(HttpServletRequest request) {
		
		TbUser user = (TbUser)request.getAttribute("user");
		
		List<TbItem> cartList = cartService.getCartList(user.getId());
		
		//把商品列表传递给jsp
		request.setAttribute("cartList", cartList);
		//返回逻辑视图

		return "order-cart";
	}
	
	//提交订单  处理前端发过来的数据，包含TbOrderItem TbOrder TbOrderShipping三个表的数据
	@RequestMapping(value="/order/create",method=RequestMethod.POST)
	public String createOrder(OrderInfo orderInfo,HttpServletRequest request) {
		
		TbUser user = (TbUser)request.getAttribute("user");
		orderInfo.setUserId(user.getId());
		orderInfo.setBuyerNick(user.getUsername());
		
		ResponseResult result = orderService.createOrder(orderInfo);
		if(result.getStatus()==200) {
			cartService.deleteCart(user.getId());
		}
		String orderId = result.getData().toString();
		// a)需要Service返回订单号
		request.setAttribute("orderId", orderId);
		request.setAttribute("payment", orderInfo.getPayment());

		return "success"; 
	}
	
}
