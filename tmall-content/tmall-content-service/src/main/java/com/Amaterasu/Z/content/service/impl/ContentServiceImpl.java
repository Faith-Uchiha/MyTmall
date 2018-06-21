package com.Amaterasu.Z.content.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.Amaterasu.Z.content.service.ContentService;
import com.Amaterasu.Z.mapper.TbContentMapper;
import com.Amaterasu.Z.pojo.EasyUIDataGridResult;
import com.Amaterasu.Z.pojo.ResponseResult;
import com.Amaterasu.Z.pojo.TbContent;
import com.Amaterasu.Z.pojo.TbContentExample;
import com.Amaterasu.Z.pojo.TbContentExample.Criteria;
import com.Amaterasu.Z.utils.JedisClient;
import com.Amaterasu.Z.utils.JsonUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class ContentServiceImpl implements ContentService{

	@Autowired
	private TbContentMapper tbContentMapper;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${CONTENT_LIST}")
	private String CONTENT_LIST;
	
	//按分类ID分页查询内容  后台工程用的
	@Override
	public EasyUIDataGridResult<TbContent> selectContentList(Long categoryId,Integer page,Integer rows) {

		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		
		//分页
		PageHelper.startPage(page, rows);
		List<TbContent> list = tbContentMapper.selectByExample(example);
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		//补全结果
		EasyUIDataGridResult<TbContent> result = new EasyUIDataGridResult<>();
		result.setRows(list);
		result.setTotal(pageInfo.getTotal());
		return result;
	}

	@Override
	public ResponseResult insertContent(TbContent tbContent) {

		Date date = new Date();
		tbContent.setCreated(date);
		tbContent.setUpdated(date);
		tbContentMapper.insert(tbContent);
		
		//缓存同步
		jedisClient.hdel(CONTENT_LIST, tbContent.getCategoryId().toString());
		return ResponseResult.ok();
	}

	@Override
	public ResponseResult editContent(TbContent tbContent) {

		Date date = new Date();
		tbContent.setUpdated(date);
		tbContentMapper.updateByPrimaryKeySelective(tbContent);
		//缓存同步
		jedisClient.hdel(CONTENT_LIST, tbContent.getCategoryId().toString());
		return ResponseResult.ok();
	}

	@Override
	public ResponseResult delContent(Long[] ids) {
		
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		List<Long> list = new ArrayList<>(Arrays.asList(ids));
		List<Long> cidList = new ArrayList<>();
		
		for(Long id:list) {
			TbContent content = tbContentMapper.selectByPrimaryKey(id);
			Long categoryId = content.getCategoryId();
			//删除商品前，获得被删除商品的CID
			if(!cidList.contains(categoryId))
				cidList.add(categoryId);
		}
		//在key=CONTENT_LIST的哈希中删掉key为cid的value
		for(Long cid:cidList) {
			//缓存同步
			jedisClient.hdel(CONTENT_LIST, cid.toString());
		}
		
		criteria.andIdIn(list);
		tbContentMapper.deleteByExample(example);
		return ResponseResult.ok();
	}

	//前台用：根据内容CID查询
	@Override
	public List<TbContent> selectContentList(Long cid) {
		
		//查询缓存
		try {
			String result = jedisClient.hget(CONTENT_LIST, cid+"");
			if(StringUtils.isNotBlank(result)){
				List<TbContent> list = JsonUtils.jsonToList(result, TbContent.class);
				return list;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		//缓存未命中则查询数据库
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(cid);
		
		//因为数据库中的content字段是text类型，所以需要BLOBs
		List<TbContent> list = tbContentMapper.selectByExampleWithBLOBs(example);
		
		//结果添加至缓存
		try {
			jedisClient.hset(CONTENT_LIST, cid+"", JsonUtils.objectToJson(list));
		}catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
