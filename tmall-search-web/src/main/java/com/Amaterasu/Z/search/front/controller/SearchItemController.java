package com.Amaterasu.Z.search.front.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.Amaterasu.Z.pojo.SearchResult;
import com.Amaterasu.Z.search.service.SearchService;

@Controller
public class SearchItemController {

	@Autowired
	private SearchService searchService;
	
	//每页显示多少条
	@Value("${SEARCH_RESULT_ROWS}")
	private Integer SEARCH_RESULT_ROWS;
	/**
	 * 根绝关键字搜索商品，并将结果分页
	 * @param keyword
	 * @param page
	 * @param model
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value="/search",method=RequestMethod.GET)
	public String searchItemList(String keyword,
			@RequestParam(name="page",defaultValue="1")Integer page,Model model) throws UnsupportedEncodingException {

		keyword = new String(keyword.getBytes("iso-8859-1"),"utf-8");
		SearchResult res = searchService.search(keyword,page,SEARCH_RESULT_ROWS);
		
		//向页面传递结果
		model.addAttribute("page", page);
		model.addAttribute("totalPages", res.getTotalPages());
		model.addAttribute("recourdCount", res.getRecordCount());
		model.addAttribute("itemList", res.getItemList());
		model.addAttribute("query", keyword); //查询关键字回显
		return "search";
	}
}
