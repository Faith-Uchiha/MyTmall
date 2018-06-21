package com.Amaterasu.Z.content.service;

import java.util.List;

import com.Amaterasu.Z.pojo.EasyUIDataGridResult;
import com.Amaterasu.Z.pojo.ResponseResult;
import com.Amaterasu.Z.pojo.TbContent;

public interface ContentService {

	EasyUIDataGridResult<TbContent> selectContentList(Long categoryId, Integer page, Integer rows);

	ResponseResult insertContent(TbContent tbContent);

	ResponseResult editContent(TbContent tbContent);

	ResponseResult delContent(Long[] ids);

	List<TbContent> selectContentList(Long CONTENT_BIG_AD_CATEGORY_ID);

}
