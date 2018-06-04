package com.Amaterasu.Z.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Amaterasu.Z.pojo.TbItem;
import com.Amaterasu.Z.service.ItemService;

@Controller
public class ItemController {
	
	@Autowired
	private ItemService itemService;
	
	@RequestMapping(value="/item/{itemId}")  //这个itemId和下面的参数名称一定要一致，否则找不到
	@ResponseBody
	public TbItem selectItemById(@PathVariable Long itemId) {
		
		TbItem item = itemService.selectItemById(itemId);
		return item;
	}
}
