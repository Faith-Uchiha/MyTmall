package com.Amaterasu.Z.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Amaterasu.Z.mapper.TbItemMapper;
import com.Amaterasu.Z.pojo.TbItem;
import com.Amaterasu.Z.pojo.TbItemExample;
import com.Amaterasu.Z.pojo.TbItemExample.Criteria;
import com.Amaterasu.Z.service.ItemService;

@Service
public class ItemServiceImpl implements ItemService {
	
	@Autowired
	private TbItemMapper itemMapper;
	
	public TbItem selectItemById(long id){
		
		TbItemExample example = new TbItemExample();
		 Criteria criteria = example.createCriteria();
		 criteria.andIdEqualTo(id);
		 
		List<TbItem> itemList = itemMapper.selectByExample(example);
		
		if(itemList!=null&&itemList.size()>0)
			return itemList.get(0);
		
		return null;
	}
	
	
}
