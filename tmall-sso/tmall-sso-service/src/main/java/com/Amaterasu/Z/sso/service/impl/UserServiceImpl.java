package com.Amaterasu.Z.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.Amaterasu.Z.jedis.JedisClient;
import com.Amaterasu.Z.mapper.TbUserMapper;
import com.Amaterasu.Z.pojo.ResponseResult;
import com.Amaterasu.Z.pojo.TbUser;
import com.Amaterasu.Z.pojo.TbUserExample;
import com.Amaterasu.Z.pojo.TbUserExample.Criteria;
import com.Amaterasu.Z.sso.service.UserService;
import com.Amaterasu.Z.utils.JsonUtils;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper tbUserMapper;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${SESSION_EXPIRE}")
	private Integer SESSION_EXPIRE;
	
	@Override
	public ResponseResult checkData(String param, int type) {

		TbUserExample tbexm = new TbUserExample();
		Criteria criteria = tbexm.createCriteria();
		// 2、查询条件根据参数动态生成。
		//1、2、3分别代表username、phone、email
		if (type == 1) {
			criteria.andUsernameEqualTo(param);
		} else if (type == 2) {
			criteria.andPhoneEqualTo(param);
		} else if (type == 3) {
			criteria.andEmailEqualTo(param);
		} else {
			return ResponseResult.build(400, "非法的参数");
		}
		//执行查询
		List<TbUser> list = tbUserMapper.selectByExample(tbexm);
		// 3、判断查询结果，如果查询到数据返回false。
		if (list == null || list.size() == 0) {
			// 4、如果没有返回true。
			return ResponseResult.ok(true);
		} 
		// 5、使用ResponseResult包装，并返回。
		return ResponseResult.ok(false);
	}

	@Override
	public ResponseResult register(TbUser user) {
		// 1、使用TbUser接收提交的请求。
		if (StringUtils.isBlank(user.getUsername())) {
			return ResponseResult.build(400, "用户名不能为空");
		}
		if (StringUtils.isBlank(user.getPassword())) {
			return ResponseResult.build(400, "密码不能为空");
		}
		//校验数据是否可用
		ResponseResult result = checkData(user.getUsername(), 1);
		if (!(boolean) result.getData()) {
			return ResponseResult.build(400, "此用户名已经被使用");
		}
		//校验电话是否可以
		if (StringUtils.isNotBlank(user.getPhone())) {
			result = checkData(user.getPhone(), 2);
			if (!(boolean) result.getData()) {
				return ResponseResult.build(400, "此手机号已经被使用");
			}
		}
		//校验email是否可用
		if (StringUtils.isNotBlank(user.getEmail())) {
			result = checkData(user.getEmail(), 3);
			if (!(boolean) result.getData()) {
				return ResponseResult.build(400, "此邮件地址已经被使用");
			}
		}
		// 2、补全TbUser其他属性。
		user.setCreated(new Date());
		user.setUpdated(new Date());
		// 3、密码要进行MD5加密。
		String md5Pass = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(md5Pass);
		// 4、把用户信息插入到数据库中。
		tbUserMapper.insert(user);
		// 5、返回ResponseResult。
		return ResponseResult.ok();

	}

	//登陆
	@Override
	public ResponseResult login(String username, String password) {
		// 1、判断用户名密码是否正确。
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		//查询用户信息
		List<TbUser> list = tbUserMapper.selectByExample(example);
		if (list == null || list.size() == 0) {
			return ResponseResult.build(400, "用户名或密码错误");
		}
		TbUser user = list.get(0);
		//校验密码
		if (!user.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))) {
			return ResponseResult.build(400, "用户名或密码错误");
		}
		// 2、登录成功后生成token。Token相当于原来的jsessionid，字符串，可以使用uuid。
		String token = UUID.randomUUID().toString();
		// 3、把用户信息保存到redis。Key就是token，value就是TbUser对象转换成json。
		// 4、使用String类型保存Session信息（这里是保存用户信息）。可以使用“前缀:token”为key
		user.setPassword(null);
		jedisClient.set("SESSION" + ":" + token, JsonUtils.objectToJson(user));
		// 5、设置key的过期时间。模拟Session的过期时间。一般半个小时。单位为秒
		jedisClient.expire("SESSION" + ":" + token, SESSION_EXPIRE); 
		// 6、返回ResponseResult包装token。
		return ResponseResult.ok(token);

	}

	public ResponseResult getUserByToken(String token) {
		// 2、根据token查询redis。
		String json = jedisClient.get("SESSION:" + token);
		if (StringUtils.isBlank(json)) {
			// 3、如果查询不到数据。返回用户已经过期。
			return ResponseResult.build(400, "用户登录已经过期，请重新登录。");
		}
		// 4、如果查询到数据，说明用户已经登录。
		// 5、需要重置key的过期时间。
		jedisClient.expire("SESSION:" + token, SESSION_EXPIRE);
		// 6、把json数据转换成TbUser对象，然后使用ResponseResult包装并返回。
		TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
		return ResponseResult.ok(user);
	}

}
