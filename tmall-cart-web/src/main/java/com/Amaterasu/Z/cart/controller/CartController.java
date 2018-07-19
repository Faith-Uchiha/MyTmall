package com.Amaterasu.Z.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Amaterasu.Z.cart.service.CartService;
import com.Amaterasu.Z.pojo.ResponseResult;
import com.Amaterasu.Z.pojo.TbItem;
import com.Amaterasu.Z.pojo.TbUser;
import com.Amaterasu.Z.service.ItemService;
import com.Amaterasu.Z.utils.CookieUtils;
import com.Amaterasu.Z.utils.JsonUtils;

@Controller
public class CartController {

	@Autowired
	private ItemService itemService;
	
	@Autowired
	private CartService cartService;
	
	@Value("${COOKIE_CART_EXPIRE}")
	private int COOKIE_CART_EXPIRE; //cookie有效期为3天 单位是秒
	
	private List<TbItem> getCartListFromCookie(HttpServletRequest request){
		
		//最后一个参数为true表明按照UTF解码
		String json = CookieUtils.getCookieValue(request, "cart",true);
		if(StringUtils.isBlank(json)) {
			return new ArrayList<>();
		}
		//如果不为空 那么说明cookie中已经有购物车了
		List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
		return list;
	}
	
	//在商品详情页面点击“添加到购物车”
	@RequestMapping(value="/cart/add/{itemId}")
	public String addCart(@PathVariable Long itemId,@RequestParam(defaultValue="1") Integer num,
			HttpServletRequest request,HttpServletResponse response) {
		
		//添加登录状态后的购物车
		//判断用户是否为登录状态
		Object object = request.getAttribute("user"); //如果这里能获取的到，说明拦截器设置了user
		if (object != null) {
			TbUser user = (TbUser) object;
			//取用户id
			Long userId = user.getId();
			//添加到服务端
			ResponseResult result = cartService.addCart(userId, itemId, num);
			return "cartSuccess";
		}

		// 1、从cookie中查询商品列表。
		List<TbItem> cartList = getCartListFromCookie(request);
		// 2、判断商品在商品列表中是否存在。
		boolean hasItem = false;
		for (TbItem tbItem : cartList) {
			//对象比较的是地址，应该是值的比较
			if (tbItem.getId() == itemId.longValue()) {
				// 3、如果存在，商品数量相加。
				tbItem.setNum(tbItem.getNum() + num);
				hasItem = true;
				break;
			}
		}
		if (!hasItem) {
			// 4、不存在，根据商品id查询商品信息。
			TbItem tbItem = itemService.selectItemById(itemId);
			//取一张图片
			String image = tbItem.getImage();
			if (StringUtils.isNoneBlank(image)) {
				String[] images = image.split(",");
				tbItem.setImage(images[0]);
			}
			//设置购买商品数量
			tbItem.setNum(num);
			// 5、把商品添加到购车列表。
			cartList.add(tbItem);
		}
		// 6、把购车商品列表写入cookie。
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), COOKIE_CART_EXPIRE, true);

		return "cartSuccess"; 
	}
	
	//添加成功后点击“去购物车结算”
	@RequestMapping(value="/cart/cart")
	public String showCartList(HttpServletRequest request, HttpServletResponse response) {
		
		//不管登没登陆，都先取cookie
		List<TbItem> cartList = getCartListFromCookie(request);
		
		//如果登陆了就先合并购物车
		Object object = request.getAttribute("user");
		if (object != null) {
			TbUser user = (TbUser) object; 
			//用户已经登录
			System.out.println("用户已经登录，用户名为：" + user.getUsername());
			//判断cookie的购物车列表是否为空
			if (!cartList.isEmpty()) {
				//合并购物车
				cartService.mergeCart(user.getId(), cartList);
				//删除cookie中的购物车
				CookieUtils.setCookie(request, response, "cart", "");
			}
			//从服务端取购物车列表
			List<TbItem> list = cartService.getCartList(user.getId());
			request.setAttribute("cartList", list);
			return "cart";
		} else {
			System.out.println("用户未登录");
		}

		//没登录就直接设置cookie中的数据
		request.setAttribute("cartList", cartList);
		return "cart";
	}
	
	//点击+ -按钮更新商品数量 AJAX请求 
	@RequestMapping(value="/cart/update/num/{itemId}/{num}")
	@ResponseBody
	public ResponseResult updateCartNum(@PathVariable Long itemId,
			@PathVariable Integer num,HttpServletRequest request,HttpServletResponse response) {
		
		//登录状态，调用cartService的方法修改redis的数量
		Object object = request.getAttribute("user");
		if (object != null) {
			TbUser user = (TbUser) object;
			//更新服务端的购物车
			cartService.updateCartNum(user.getId(), itemId, num);
			return ResponseResult.ok();
		}

		
		List<TbItem> list = getCartListFromCookie(request);
		for(TbItem item:list) {
			if(item.getId().longValue()==itemId) {
				item.setNum(num);
				break;
			}
		}
		//回写cookie
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(list), COOKIE_CART_EXPIRE, true);
		return ResponseResult.ok();
	}
	
	//删除商品
	@RequestMapping(value="/cart/delete/{itemId}")
	public String deleteCartItem(@PathVariable Long itemId,HttpServletRequest request
			,HttpServletResponse response) {
		
		//如果处于登录状态，则删除redis记录
		//判断用户登录状态
		Object object = request.getAttribute("user");
		if (object != null) {
			TbUser user = (TbUser) object;
			//删除服务端的购物车商品
			cartService.deleteCartItem(user.getId(), itemId);
			return "redirect:/cart/cart.html";
		}

		
		
		List<TbItem> list = getCartListFromCookie(request);
		for(TbItem item:list) {
			if(item.getId().longValue()==itemId) {
				list.remove(item); //找到后删掉
				break;
			}
		}
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(list), COOKIE_CART_EXPIRE, true);
		return "redirect:/cart/cart.html";
	}
}
