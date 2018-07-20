package com.Amaterasu.Z.order.service;

import com.Amaterasu.Z.order.pojo.OrderInfo;
import com.Amaterasu.Z.pojo.ResponseResult;

public interface OrderService {
	
	public ResponseResult createOrder(OrderInfo orderInfo);
}
