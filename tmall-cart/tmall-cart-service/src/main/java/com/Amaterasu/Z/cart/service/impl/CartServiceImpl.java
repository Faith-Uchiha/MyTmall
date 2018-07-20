package com.Amaterasu.Z.cart.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.Amaterasu.Z.cart.service.CartService;
import com.Amaterasu.Z.jedis.JedisClient;
import com.Amaterasu.Z.mapper.TbItemMapper;
import com.Amaterasu.Z.pojo.ResponseResult;
import com.Amaterasu.Z.pojo.TbItem;
import com.Amaterasu.Z.utils.JsonUtils;

@Service
public class CartServiceImpl implements CartService{

	@Value("${REDIS_CART_PRE}")
	private String REDIS_CART_PRE;

	@Autowired
	private TbItemMapper itemMapper; //如果redis没有商品 就需要去数据库查
	@Autowired
	private JedisClient jedisClient;

	@Override
	public ResponseResult addCart(long userId, long itemId, int num) {
		// a)判断购物车中是否有此商品
		Boolean flag = jedisClient.hexists(REDIS_CART_PRE + ":" + userId, itemId + "");
		// b)如果有，数量相加
		if (flag) {
			//从hash中取商品数据
			String json = jedisClient.hget(REDIS_CART_PRE + ":" + userId, itemId + "");
			//转换成java对象
			TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
			//数量相加
			tbItem.setNum(tbItem.getNum() + num);
			//写入hash
			jedisClient.hset(REDIS_CART_PRE + ":" + userId, itemId + "", JsonUtils.objectToJson(tbItem));
			//返回添加成功
			return ResponseResult.ok();
		}
		// c)如果没有，根据商品id查询商品信息。
		TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
		//设置商品数量
		tbItem.setNum(num);
		String image = tbItem.getImage();
		//取一张图片
		if (StringUtils.isNotBlank(image)) {
			tbItem.setImage(image.split(",")[0]);
		}
		// d)把商品信息添加到购物车
		jedisClient.hset(REDIS_CART_PRE + ":" + userId, itemId + "", JsonUtils.objectToJson(tbItem));
		return ResponseResult.ok();

	}

	//将cookie的购物车（cartList）合并到redis
	@Override
	public ResponseResult mergeCart(Long id, List<TbItem> cartList) {
		//遍历商品列表
		for (TbItem tbItem : cartList) {
			addCart(id, tbItem.getId(), tbItem.getNum());
		}
		return ResponseResult.ok();
	}

	//根据用户id获取redis的购物车列表
	@Override
	public List<TbItem> getCartList(Long id) {
		//从redis中根据用户id查询商品列表
		List<String> strList = jedisClient.hvals(REDIS_CART_PRE + ":" + id);
		List<TbItem> resultList = new ArrayList<>();
		//把json列表转换成TbItem列表    从JSON商品列表中还原出商品对象
		for (String string : strList) {
			TbItem tbItem = JsonUtils.jsonToPojo(string, TbItem.class);
			//添加到列表
			resultList.add(tbItem);
		}
		return resultList;
	}

	//更改redis的商品数量
	@Override
	public ResponseResult updateCartNum(long userId,long itemId, int num) {
		//从hash中取商品信息
		String json = jedisClient.hget(REDIS_CART_PRE + ":" + userId, itemId + "");
		//转换成java对象
		TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
		//更新数量
		tbItem.setNum(num);
		//写入hash
		jedisClient.hset(REDIS_CART_PRE + ":" + userId, itemId + "", JsonUtils.objectToJson(tbItem));
		return ResponseResult.ok();

	}

	//删除选中的商品
	@Override
	public ResponseResult deleteCartItem(long userId, long itemId) {
		// 根据商品id删除hash中对应的商品数据。
		jedisClient.hdel(REDIS_CART_PRE + ":" + userId, itemId + "");
		return ResponseResult.ok();

	}

	//写入表成功后就删除购物车
	@Override
	public ResponseResult deleteCart(long id) {
		
		jedisClient.del(REDIS_CART_PRE+":"+id); 
		return ResponseResult.ok();
	}
}
