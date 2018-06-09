package com.Amaterasu.Z.service;

import java.util.List;

import com.Amaterasu.Z.pojo.EasyUITreeNode;

public interface ItemCatService {
	public List<EasyUITreeNode> selectItemCatList(long parentId);
}
