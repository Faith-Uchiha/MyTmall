package com.Amaterasu.Z.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Amaterasu.Z.pojo.ResponseResult;
import com.Amaterasu.Z.search.service.SearchService;

@Controller
public class SearchItemController {
	
	@Autowired 
	private SearchService searchService;
	
	/**
	 * 后台管理系统中向索引库导入商品数据
	 * @return
	 */
	@RequestMapping(value="/index/item/import",method=RequestMethod.POST)
	@ResponseBody
	public ResponseResult importIndex() {
		return searchService.importItems();
	}
}
