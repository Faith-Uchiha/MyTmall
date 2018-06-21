package com.Amaterasu.Z.front.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Amaterasu.Z.content.service.ContentService;
import com.Amaterasu.Z.pojo.TbContent;

@Controller
public class IndexController {

	@Value("${CONTENT_BIG_AD_CATEGORY_ID}")
	private Long CONTENT_BIG_AD_CATEGORY_ID;
	
	@Autowired
	private ContentService contentService;
	
	@RequestMapping(value="index")
	public String showIndex(Model model) {
		
		List<TbContent> ad1List = contentService.selectContentList(CONTENT_BIG_AD_CATEGORY_ID);
		model.addAttribute("ad1List", ad1List);
		return "index";
	}
}
