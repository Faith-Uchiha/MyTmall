package com.Amaterasu.Z.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Amaterasu.Z.content.service.ContentCategoryService;
import com.Amaterasu.Z.pojo.EasyUITreeNode;
import com.Amaterasu.Z.pojo.ResponseResult;

@Controller
public class ContentCategoryController {

	@Autowired
	private ContentCategoryService contentCatService;
	
	/**
	 * 内容分类查询
	 * @param parentId
	 * @return JSON字符串 格式为[{"id":xxx,"text":xxx,"state":xxx},{},{}]
	 */
	@RequestMapping(value="/content/category/list",method=RequestMethod.GET)
	@ResponseBody
	public List<EasyUITreeNode> showContentCategoryList(
			@RequestParam(name="id",defaultValue="0")long parentId){
		
		return contentCatService.selectContentCatList(parentId);
	}
	
	/**
	 * 内容分类添加
	 * @param parentId
	 * @param name
	 * @return 插入成功返回状态码200
	 */
	@RequestMapping(value="/content/category/create")
	@ResponseBody
	public ResponseResult insertContentCategory(Long parentId,String name) {
		
		return contentCatService.insertContentCategory(parentId,name);
	}
	
	/**
	 * 内容分类编辑
	 * @param id
	 * @param name
	 * @return 插入成功返回状态码200
	 */
	@RequestMapping(value="/content/category/update")
	@ResponseBody
	public ResponseResult editContentCategory(Long id,String name) {
		
		return contentCatService.editContentCategory(id,name);
	}
	
	/**
	 * 内容分类删除
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/content/category/delete/")
	@ResponseBody
	public ResponseResult delContentCategory(Long id) {
		
		return contentCatService.delContentCategory(id);
	}
}
