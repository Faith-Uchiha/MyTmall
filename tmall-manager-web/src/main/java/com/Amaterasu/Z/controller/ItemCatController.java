package com.Amaterasu.Z.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Amaterasu.Z.pojo.EasyUITreeNode;
import com.Amaterasu.Z.service.ItemCatService;

@Controller
public class ItemCatController {

	@Autowired
	private ItemCatService itemCatService;
	
	@RequestMapping(value="/item/cat/list")
	@ResponseBody
	public List<EasyUITreeNode> selectItemCatList(
			@RequestParam(name="id",defaultValue="0")long parentId){
		
		return itemCatService.selectItemCatList(parentId);
	}
}
