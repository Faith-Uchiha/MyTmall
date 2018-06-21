package com.Amaterasu.Z.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Amaterasu.Z.content.service.ContentService;
import com.Amaterasu.Z.pojo.EasyUIDataGridResult;
import com.Amaterasu.Z.pojo.ResponseResult;
import com.Amaterasu.Z.pojo.TbContent;

@Controller
public class ContentController {

	@Autowired
	private ContentService contentService;
	
	/**
	 * 内容 查询
	 * @param categoryId 分页参数page,rows
	 * @return
	 */
	@RequestMapping(value="/content/query/list",method=RequestMethod.GET)
	@ResponseBody
	public EasyUIDataGridResult<TbContent> selectContentList(
			@RequestParam(name="categoryId" ,defaultValue="0") Long categoryId,Integer page,Integer rows) {
		
		//代码是对的，一开始什么都不显示是因为传过来的categoryId=0 数据库没有数据
		return contentService.selectContentList(categoryId,page,rows);
	}
	
	/**
	 * 内容添加
	 * @param tbContent
	 * @return
	 */
	@RequestMapping(value="/content/save",method=RequestMethod.POST)
	@ResponseBody
	public ResponseResult insertContent(TbContent tbContent) {
		
		return contentService.insertContent(tbContent);
	}
	
	/**
	 * 内容编辑
	 * @param tbContent
	 * @return
	 */
	@RequestMapping(value="/rest/content/edit",method=RequestMethod.POST)
	@ResponseBody
	public ResponseResult editContent(TbContent tbContent) {
		
		return contentService.editContent(tbContent);
	}
	
	
	/**
	 * 内容删除  可以删除多个id的内容
	 * @param ids 数组
	 * @return
	 */
	@RequestMapping(value="/content/delete",method=RequestMethod.POST)
	@ResponseBody
	public ResponseResult delContent(Long[] ids) {
		
		return contentService.delContent(ids);
	}
}
