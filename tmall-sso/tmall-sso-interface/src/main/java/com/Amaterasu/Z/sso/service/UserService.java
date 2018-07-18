package com.Amaterasu.Z.sso.service;

import com.Amaterasu.Z.pojo.ResponseResult;
import com.Amaterasu.Z.pojo.TbUser;

public interface UserService {
	
	//数据校验
	public ResponseResult checkData(String param,int type);
	
	//用户注册
	public ResponseResult register(TbUser user);
	
	//用户登陆
	public ResponseResult login(String username,String password);
	
	//从session中获取登录信息
	public ResponseResult getUserByToken(String token);
}
