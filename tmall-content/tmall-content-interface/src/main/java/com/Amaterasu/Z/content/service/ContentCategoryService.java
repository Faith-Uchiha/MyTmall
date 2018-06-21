package com.Amaterasu.Z.content.service;

import java.util.List;

import com.Amaterasu.Z.pojo.EasyUITreeNode;
import com.Amaterasu.Z.pojo.ResponseResult;

public interface ContentCategoryService {

	List<EasyUITreeNode> selectContentCatList(long parentId);

	ResponseResult insertContentCategory(Long parentId, String name);

	ResponseResult editContentCategory(Long id, String name);

	ResponseResult delContentCategory(Long id);

}
