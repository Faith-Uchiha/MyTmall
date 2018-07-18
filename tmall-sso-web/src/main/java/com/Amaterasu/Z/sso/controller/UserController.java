package com.Amaterasu.Z.sso.controller;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Amaterasu.Z.pojo.ResponseResult;
import com.Amaterasu.Z.pojo.TbUser;
import com.Amaterasu.Z.sso.service.UserService;
import com.Amaterasu.Z.utils.CookieUtils;
import com.Amaterasu.Z.utils.JsonUtils;

@Controller
public class UserController {

	@Autowired
	private UserService userService;
	
	@Value("${COOKIE_TOKEN_KEY}")
	private String COOKIE_TOKEN_KEY;
	
	//数据校验
	@RequestMapping("/user/check/{param}/{type}")
	@ResponseBody
	public ResponseResult checkData(@PathVariable String param, @PathVariable Integer type) {
		
		ResponseResult ResponseResult = userService.checkData(param, type);
		return ResponseResult;
		
	}
	
	//用户注册
	@RequestMapping(value="/user/register", method=RequestMethod.POST)
	@ResponseBody
	public ResponseResult register(TbUser user) {
		ResponseResult result = userService.register(user);
		return result;
	}

	//注意这个URL不能重复请求，否则会导致重复登录（redis里有多个session）
	//用户登录
	@RequestMapping(value="/user/login",method=RequestMethod.POST)
	@ResponseBody
	public ResponseResult login(String username,String password,
			HttpServletRequest request,HttpServletResponse resp)
	{
		
		ResponseResult result = userService.login(username, password);
		//登陆成功则写cookie
		if(result.getStatus()==200) {
			String token = result.getData().toString();
			CookieUtils.setCookie(request,resp,COOKIE_TOKEN_KEY,token);;
		}
		return result;
	}
	
	
	//根据token查redis
	@RequestMapping(value="/user/token/{token}",
			produces=MediaType.APPLICATION_JSON_UTF8_VALUE) //处理跨域请求
	@ResponseBody
	public String getUserByToken(@PathVariable String token,String callback) {
		ResponseResult result = userService.getUserByToken(token);
		//判断是否是jsonp请求
		if(StringUtils.isNotBlank(callback)) {
			//组装成json
			return callback+"("+JsonUtils.objectToJson(result)+");";
		}
		return JsonUtils.objectToJson(result);
	}

}
