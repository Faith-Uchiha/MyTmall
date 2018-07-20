package com.Amaterasu.Z.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.Amaterasu.Z.jedis.JedisClient;
import com.Amaterasu.Z.mapper.TbOrderItemMapper;
import com.Amaterasu.Z.mapper.TbOrderMapper;
import com.Amaterasu.Z.mapper.TbOrderShippingMapper;
import com.Amaterasu.Z.order.pojo.OrderInfo;
import com.Amaterasu.Z.order.service.OrderService;
import com.Amaterasu.Z.pojo.ResponseResult;
import com.Amaterasu.Z.pojo.TbOrderItem;
import com.Amaterasu.Z.pojo.TbOrderShipping;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;
	
	//需要用Redis生成订单ID
	@Autowired
	private JedisClient jedisClient;
	
	//订单表
	@Value("${ORDER_ID_GEN_KEY}")
	private String ORDER_GEN_KEY;
	@Value("${ORDER_ID_START}")
	private String ORDER_ID_BEGIN;
	
	//订单中的商品ID
	@Value("${ORDER_DETAIL_ID_GEN_KEY}")
	private String ORDER_DETAIL_ID_GEN_KEY;

	
	@Override
	public ResponseResult createOrder(OrderInfo orderInfo) {
		// 1、接收表单的数据
		// 2、生成订单id
		if (!jedisClient.exists(ORDER_GEN_KEY)) {
			//设置初始值
			jedisClient.set(ORDER_GEN_KEY, ORDER_ID_BEGIN);
		}
		String orderId = jedisClient.incr(ORDER_GEN_KEY).toString();
		orderInfo.setOrderId(orderId);
		orderInfo.setPostFee("0");
		//1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
		orderInfo.setStatus(1);
		Date date = new Date();
		orderInfo.setCreateTime(date);
		orderInfo.setUpdateTime(date);
		// 3、向订单表插入数据。
		orderMapper.insert(orderInfo);
		// 4、向订单明细表插入数据
		List<TbOrderItem> orderItems = orderInfo.getOrderItems();
		for (TbOrderItem tbOrderItem : orderItems) {
			//生成明细id
			Long orderItemId = jedisClient.incr(ORDER_DETAIL_ID_GEN_KEY);
			tbOrderItem.setId(orderItemId.toString());
			tbOrderItem.setOrderId(orderId);
			//插入数据
			orderItemMapper.insert(tbOrderItem);
		}
		// 5、向订单物流表插入数据。
		TbOrderShipping orderShipping = orderInfo.getOrderShipping();
		orderShipping.setOrderId(orderId);
		orderShipping.setCreated(date);
		orderShipping.setUpdated(date);
		orderShippingMapper.insert(orderShipping);
		// 6、返回正确状态码 和订单号。
		return ResponseResult.ok(orderId);

	}

}
