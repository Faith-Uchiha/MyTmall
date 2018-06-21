package com.Amaterasu.Z.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Amaterasu.Z.content.service.ContentCategoryService;
import com.Amaterasu.Z.mapper.TbContentCategoryMapper;
import com.Amaterasu.Z.pojo.EasyUITreeNode;
import com.Amaterasu.Z.pojo.ResponseResult;
import com.Amaterasu.Z.pojo.TbContentCategory;
import com.Amaterasu.Z.pojo.TbContentCategoryExample;
import com.Amaterasu.Z.pojo.TbContentCategoryExample.Criteria;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	@Autowired
	private TbContentCategoryMapper tbContentCatMapper;
	
	@Override
	public List<EasyUITreeNode> selectContentCatList(long parentId) {

		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbContentCategory> list = tbContentCatMapper.selectByExample(example);
//		for(int i=0;i<list.size();i++) {
//			System.out.println(list.get(i).getId());
//		}
		List<EasyUITreeNode> treeList = new ArrayList<>();
		
		for(TbContentCategory conCat:list) {
			
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(conCat.getId());
			node.setText(conCat.getName());
			node.setState(conCat.getIsParent()?"closed":"open");
			treeList.add(node);
		}
		//System.out.println("treeList size:"+treeList.size());
		return treeList;
	}

	@Override
	public ResponseResult insertContentCategory(Long parentId, String name) {
		
		TbContentCategory record = new TbContentCategory();
		Date date = new Date();
		record.setCreated(date);
		record.setUpdated(date);
//		record.setId(IDUtils.);//内容分类的ID自动递增
		record.setParentId(parentId);
		record.setName(name);
		record.setStatus(1); //1 正常 2 删除
		record.setSortOrder(1);
		record.setIsParent(false);//新添加的为 叶子结点
		
		//若被插入的节点为叶子结点则改为父节点
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(parentId);
		List<TbContentCategory> list = tbContentCatMapper.selectByExample(example);
		TbContentCategory pNode = list.get(0);
		if(!pNode.getIsParent()) {
			pNode.setIsParent(true);
			tbContentCatMapper.updateByPrimaryKey(pNode);
		}
		//插入新增节点	
		tbContentCatMapper.insert(record);
		ResponseResult result = ResponseResult.ok(record); //前端需要data.data.id 所以需要传回对象
		return result;
	}

	@Override
	public ResponseResult editContentCategory(Long id, String name) {

		TbContentCategory tbContentCat = new TbContentCategory();
		tbContentCat.setId(id);
		tbContentCat.setName(name);
		tbContentCat.setUpdated(new Date());
		tbContentCatMapper.updateByPrimaryKeySelective(tbContentCat);
		return ResponseResult.ok(tbContentCat);
	}

	//递归删除节点
	@Override
	public ResponseResult delContentCategory(Long id) {

		TbContentCategory tbConCat = tbContentCatMapper.selectByPrimaryKey(id);
		
		TbContentCategoryExample examplePid = new TbContentCategoryExample();
		Criteria criteriaPid = examplePid.createCriteria();
		Long pid = tbConCat.getParentId();
		criteriaPid.andParentIdEqualTo(pid);
		
		
		if(!tbConCat.getIsParent()) {
			tbContentCatMapper.deleteByPrimaryKey(id); //删除叶节点
			
			//先删除然后再count
			int count = tbContentCatMapper.countByExample(examplePid);
			if(count==0) {
				TbContentCategory conCatParent = new TbContentCategory();
				conCatParent.setId(pid);
				conCatParent.setIsParent(false); //修改为叶节点
				conCatParent.setUpdated(new Date());
				tbContentCatMapper.updateByPrimaryKeySelective(conCatParent);
			}
			
			return ResponseResult.ok();
		}
		
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(id);
		
		List<TbContentCategory> children = tbContentCatMapper.selectByExample(example);
		for(TbContentCategory con:children) {
			delContentCategory(con.getId());
		}
		tbContentCatMapper.deleteByPrimaryKey(id); //删除当前传入的父节点
		
		//判断删除节点的父节点是否还有子节点，没有则将isParent改为false
		int count = tbContentCatMapper.countByExample(examplePid);
		if(count==0) {
			TbContentCategory conCatParent = new TbContentCategory();
			conCatParent.setId(pid);
			conCatParent.setIsParent(false); //修改为叶节点
			conCatParent.setUpdated(new Date());
			tbContentCatMapper.updateByPrimaryKeySelective(conCatParent);
		}
		return ResponseResult.ok();
	}

}
