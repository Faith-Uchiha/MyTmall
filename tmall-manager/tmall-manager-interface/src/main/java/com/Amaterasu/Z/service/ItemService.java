package com.Amaterasu.Z.service;

import com.Amaterasu.Z.pojo.EasyUIDataGridResult;
import com.Amaterasu.Z.pojo.ResponseResult;
import com.Amaterasu.Z.pojo.TbItem;

public interface ItemService {
	
	public TbItem selectItemById(long id);

	public EasyUIDataGridResult<TbItem> selectItemListByPage(Integer page, Integer rows);

	public ResponseResult insertItem(TbItem item, String desc);
}
