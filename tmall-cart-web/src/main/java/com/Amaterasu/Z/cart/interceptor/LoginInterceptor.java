package com.Amaterasu.Z.cart.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.Amaterasu.Z.pojo.ResponseResult;
import com.Amaterasu.Z.pojo.TbUser;
import com.Amaterasu.Z.sso.service.UserService;
import com.Amaterasu.Z.utils.CookieUtils;

public class LoginInterceptor implements HandlerInterceptor {

	@Value("${COOKIE_TOKEN_KEY}")
	private String COOKIE_TOKEN_KEY;
	
	@Autowired
	private UserService userService;

	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 1、实现一个HandlerInterceptor接口。
		// 2、在执行handler方法之前做业务处理
		// 3、从cookie中取token。使用CookieUtils工具类实现。
		String token = CookieUtils.getCookieValue(request, COOKIE_TOKEN_KEY);
		// 4、没有取到token，用户未登录。放行
		if (StringUtils.isBlank(token)) {
			return true;
		}
		// 5、取到token，调用sso系统的服务，根据token查询用户信息。
		ResponseResult result = userService.getUserByToken(token);
		// 6、没有返回用户信息。登录已经过期，未登录，放行。
		if (result.getStatus() != 200) {
			return true;
		}
		// 7、返回用户信息。用户是登录状态。可以把用户对象保存到request中，在Controller中可以通过判断request中是否包含用户对象，确定是否为登录状态。
		TbUser user = (TbUser) result.getData();
		request.setAttribute("user", user);
		//返回true放行
		//返回false拦截
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
