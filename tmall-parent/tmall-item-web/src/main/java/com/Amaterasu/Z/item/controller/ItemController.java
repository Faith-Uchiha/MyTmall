package com.Amaterasu.Z.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Amaterasu.Z.item.pojo.Item;
import com.Amaterasu.Z.pojo.TbItem;
import com.Amaterasu.Z.pojo.TbItemDesc;
import com.Amaterasu.Z.service.ItemService;

@Controller
public class ItemController {

	@Autowired
	private ItemService itemService;
	@RequestMapping("/item/{itemId}")
	public String showItemInfo(@PathVariable Long itemId,Model model) {
		
		//获取商品
		TbItem tbItem = itemService.selectItemById(itemId);
		//包含images属性的商品
		Item item = new Item(tbItem);
		
		//获取商品详情
		TbItemDesc itemDesc = itemService.selectItemDescById(itemId);
		model.addAttribute("item", item);
		model.addAttribute("itemDesc", itemDesc);
		return "item";
	}
}
