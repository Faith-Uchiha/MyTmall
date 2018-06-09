package com.Amaterasu.Z.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Amaterasu.Z.mapper.TbItemCatMapper;
import com.Amaterasu.Z.pojo.EasyUITreeNode;
import com.Amaterasu.Z.pojo.TbItemCat;
import com.Amaterasu.Z.pojo.TbItemCatExample;
import com.Amaterasu.Z.pojo.TbItemCatExample.Criteria;
import com.Amaterasu.Z.service.ItemCatService;

@Service
public class ItemCatServiceImpl implements ItemCatService{

	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Override
	public List<EasyUITreeNode> selectItemCatList(long parentId) {

		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbItemCat> itemCats = itemCatMapper.selectByExample(example);
		List<EasyUITreeNode> nodeList = new ArrayList<>();
		//查出来的一个商品类目对应一个节点，所以要传回去一个list的json
		
		for(TbItemCat cat : itemCats) {
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(cat.getId());
			node.setState(cat.getIsParent()?"closed":"open");
			node.setText(cat.getName());
			nodeList.add(node);//别忘了添加到列表
		}
		
		return nodeList;
	}

}
