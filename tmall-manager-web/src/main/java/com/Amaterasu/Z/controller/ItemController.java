package com.Amaterasu.Z.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Amaterasu.Z.pojo.EasyUIDataGridResult;
import com.Amaterasu.Z.pojo.ResponseResult;
import com.Amaterasu.Z.pojo.TbItem;
import com.Amaterasu.Z.service.ItemService;

@Controller
public class ItemController {
	
	@Autowired
	private ItemService itemService;
	
	/**
	 * 根据商品ID查询
	 * @param itemId
	 * @return item
	 */
	@RequestMapping(value="/item/{itemId}")  //这个itemId和下面的参数名称一定要一致，否则找不到
	@ResponseBody
	public TbItem selectItemById(@PathVariable Long itemId) {
		
		TbItem item = itemService.selectItemById(itemId);
		return item;
	}
	
	/**
	 * 根据分页信息查询商品列表
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping(value="/item/list",method=RequestMethod.GET)
	@ResponseBody
	public EasyUIDataGridResult<TbItem> selectItemList(Integer page,Integer rows) {
		
		EasyUIDataGridResult<TbItem> result = itemService.selectItemListByPage(page,rows);
		return result;
	}
	
	/**
	 * 新增商品
	 * @param item商品 desc商品描述
	 * @return 返回一个含有状态码200的对象
	 */
	@RequestMapping(value="/item/save")
	@ResponseBody
	public ResponseResult insertItem(TbItem item,String desc) {
	
		return itemService.insertItem(item,desc);
	}
	
	
}
