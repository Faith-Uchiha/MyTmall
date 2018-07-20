package com.Amaterasu.Z.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.Amaterasu.Z.cart.service.CartService;
import com.Amaterasu.Z.pojo.ResponseResult;
import com.Amaterasu.Z.pojo.TbItem;
import com.Amaterasu.Z.pojo.TbUser;
import com.Amaterasu.Z.sso.service.UserService;
import com.Amaterasu.Z.utils.CookieUtils;
import com.Amaterasu.Z.utils.JsonUtils;

public class LoginInterceptor implements HandlerInterceptor {

	@Autowired
	private CartService cartService;
	
	@Autowired
	private UserService userService;
	
	@Value("${COOKIE_TOKEN_KEY}")
	private String COOKIE_TOKEN_KEY;
	
	@Value("${SSO_URL}")
	private String SSO_URL;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 1、从cookie中取token  token就是一串UUID
		String token = CookieUtils.getCookieValue(request, COOKIE_TOKEN_KEY);
		// 2、如果没有取到，没有登录，跳转到sso系统的登录页面。拦截
		if (StringUtils.isBlank(token)) {
			//跳转到登录页面
			response.sendRedirect(SSO_URL + "/page/login?redirect=" + request.getRequestURL());
			return false;
		}
		// 3、如果取到token。判断登录是否过期，需要调用sso系统的服务，根据token取用户信息
		ResponseResult result = userService.getUserByToken(token);
		// 4、如果没有取到用户信息，登录已经过期，重新登录。跳转到登录页面。拦截
		if (result.getStatus() != 200) {
			response.sendRedirect(SSO_URL + "/page/login?redirect=" + request.getRequestURL());
			return false;
		}
		// 5、如果取到用户信息，用户已经是登录状态，把用户信息保存到request中。放行
		TbUser user = (TbUser) result.getData();
		request.setAttribute("user", user);
		// 6、判断cookie中是否有购物车信息，如果有合并购物车
		String json = CookieUtils.getCookieValue(request, "cart", true);
		if (StringUtils.isNotBlank(json)) {
			cartService.mergeCart(user.getId(), JsonUtils.jsonToList(json, TbItem.class));
			//删除cookie中的购物车数据
			CookieUtils.setCookie(request, response, "cart", "");
		}
		//放行
		return true;
	}


	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
