package com.Amaterasu.Z.cart.service;

import java.util.List;


import com.Amaterasu.Z.pojo.ResponseResult;
import com.Amaterasu.Z.pojo.TbItem;

public interface CartService {

	public ResponseResult addCart(long userId,long itemId,int num);

	public ResponseResult mergeCart(Long id, List<TbItem> cartList);

	public List<TbItem> getCartList(Long id);

	public ResponseResult updateCartNum(long userId,long itemId,int num);

	public ResponseResult deleteCartItem(long id, long itemId);
		
}
