package com.Amaterasu.Z.order.pojo;

import java.util.List;

import com.Amaterasu.Z.pojo.TbOrder;
import com.Amaterasu.Z.pojo.TbOrderItem;
import com.Amaterasu.Z.pojo.TbOrderShipping;

public class OrderInfo extends TbOrder {

	List<TbOrderItem> orderItems;
	TbOrderShipping orderShipping;
	public List<TbOrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<TbOrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	public TbOrderShipping getOrderShipping() {
		return orderShipping;
	}
	public void setOrderShipping(TbOrderShipping orderShipping) {
		this.orderShipping = orderShipping;
	}
}
